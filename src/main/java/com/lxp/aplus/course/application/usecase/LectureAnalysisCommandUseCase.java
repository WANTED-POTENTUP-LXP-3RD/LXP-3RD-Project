package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import com.lxp.aplus.course.application.command.SaveLectureAnalysisCallbackCommand;
import com.lxp.aplus.course.application.port.out.LectureAnalysisPort;
import com.lxp.aplus.course.application.result.LectureAnalysisStartResult;
import com.lxp.aplus.course.domain.AnalysisStatus;
import com.lxp.aplus.course.domain.LectureKeyword;
import com.lxp.aplus.course.domain.LectureKeywordRepository;
import com.lxp.aplus.course.domain.LectureResourceRepository;
import com.lxp.aplus.course.domain.LectureResourceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class LectureAnalysisCommandUseCase {
    private final LectureResourceRepository lectureResourceRepository;
    private final LectureKeywordRepository lectureKeywordRepository;
    private final LectureAnalysisPort lectureAnalysisPort;

    @Value("${analysis.callback-secret}")
    private String callbackSecret;

    public LectureAnalysisStartResult startAnalysis(Long lectureResourceId) {
        LectureResourceV2 lectureResource = lectureResourceRepository.findById(lectureResourceId)
                .orElseThrow(() -> new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_NOT_EXIST));

        if (lectureResource.getAnalysisStatus() == AnalysisStatus.PROCESSING) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_ANALYSIS_ALREADY_PROCESSING);
        }

        lectureResource.updateAnalysisStatus(AnalysisStatus.PROCESSING);

        String requestId = UUID.randomUUID().toString();
        try {
            lectureAnalysisPort.startAnalysisAsync(lectureResource.getId(), lectureResource.getFileKey(), requestId);
        } catch (Exception e) {
            lectureResource.updateAnalysisStatus(AnalysisStatus.FAILED);
            log.error("Analysis start request failed. lectureResourceId={}, requestId={}", lectureResourceId, requestId, e);
            throw new BusinessException(LectureResourceErrorCode.STORAGE_CLIENT_INTERNAL_ERROR);
        }

        return LectureAnalysisStartResult.from(lectureResource.getAnalysisStatus());
    }

    public void handleCallback(String secretHeader, SaveLectureAnalysisCallbackCommand command) {
        validateSecret(secretHeader, command.lectureResourceId(), command.requestId());

        LectureResourceV2 lectureResource = lectureResourceRepository.findById(command.lectureResourceId())
                .orElseThrow(() -> new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_NOT_EXIST));

        if (isDuplicateTerminalCallback(lectureResource.getAnalysisStatus(), command.status())) {
            log.info(
                    "Ignoring duplicate callback for terminal state. lectureResourceId={}, requestId={}, currentStatus={}, callbackStatus={}",
                    command.lectureResourceId(),
                    command.requestId(),
                    lectureResource.getAnalysisStatus(),
                    command.status()
            );
            return;
        }

        if (command.status() == AnalysisStatus.SUCCESS) {
            if (command.keywords() == null) {
                throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT);
            }
            lectureKeywordRepository.deleteByLectureResourceId(lectureResource.getId());

            List<LectureKeyword> keywords = command.keywords().stream()
                    .map(keyword -> LectureKeyword.create(
                            lectureResource.getId(),
                            keyword.keyword(),
                            keyword.importance()))
                    .toList();

            lectureKeywordRepository.saveAll(keywords);
            lectureResource.updateAnalysisStatus(AnalysisStatus.SUCCESS);
        } else if (command.status() == AnalysisStatus.FAILED) {
            lectureResource.updateAnalysisStatus(AnalysisStatus.FAILED);
        } else {
            throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT);
        }
    }

    private void validateSecret(String secretHeader, Long lectureResourceId, String requestId) {
        if (secretHeader == null || !secretHeader.equals(callbackSecret)) {
            log.warn("Rejected callback due to invalid secret. lectureResourceId={}, requestId={}", lectureResourceId, requestId);
            throw new BusinessException(GlobalErrorCode.FORBIDDEN);
        }
    }

    private boolean isDuplicateTerminalCallback(AnalysisStatus currentStatus, AnalysisStatus callbackStatus) {
        if (currentStatus == AnalysisStatus.SUCCESS) {
            return true;
        }
        return currentStatus == AnalysisStatus.FAILED && callbackStatus == AnalysisStatus.FAILED;
    }
}

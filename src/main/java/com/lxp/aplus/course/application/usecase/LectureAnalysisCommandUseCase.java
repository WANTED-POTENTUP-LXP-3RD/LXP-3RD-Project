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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
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

        lectureResource.updateAnalysisStatus(AnalysisStatus.PROCESSING);

        String requestId = UUID.randomUUID().toString();
        lectureAnalysisPort.startAnalysisAsync(lectureResource.getId(), lectureResource.getFileKey(), requestId);

        return LectureAnalysisStartResult.from(lectureResource.getAnalysisStatus());
    }

    public void handleCallback(String secretHeader, SaveLectureAnalysisCallbackCommand command) {
        validateSecret(secretHeader);

        LectureResourceV2 lectureResource = lectureResourceRepository.findById(command.lectureResourceId())
                .orElseThrow(() -> new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_NOT_EXIST));

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

    private void validateSecret(String secretHeader) {
        if (secretHeader == null || !secretHeader.equals(callbackSecret)) {
            throw new BusinessException(GlobalErrorCode.FORBIDDEN);
        }
    }
}

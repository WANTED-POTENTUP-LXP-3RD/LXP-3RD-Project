package com.lxp.aplus.progress.application.service;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.progress.application.command.ProgressUpdateCommand;
import com.lxp.aplus.progress.application.dto.response.ProgressUpdateResponse;
import com.lxp.aplus.progress.application.port.in.ProgressCommandUseCase;
import com.lxp.aplus.progress.application.port.out.EnrollmentReader;
import com.lxp.aplus.progress.application.port.out.LectureQueryPort;
import com.lxp.aplus.progress.application.port.out.dto.EnrollmentStatusDto;
import com.lxp.aplus.progress.application.port.out.dto.LectureDurationDto;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProgressCommandService implements ProgressCommandUseCase {

    private final ProgressRepository progressRepository;
    private final EnrollmentReader enrollmentReader;
    private final LectureQueryPort lectureProvider;

    @Override
    public ProgressUpdateResponse updateProgress(Long userId, Long courseId, ProgressUpdateCommand command) {
        EnrollmentStatusDto enrollmentStatusDto = enrollmentReader.findEnrollment(userId, courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        if (enrollmentStatusDto.isExpired()) {
            throw new BusinessException(ProgressErrorCode.CANNOT_UPDATE_EXPIRED_ENROLLMENT);
        }

        LectureDurationDto lectureDurationDto = lectureProvider.getLectureInfo(command.resourceId());
        if (lectureDurationDto == null) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
        }

        Progress progress = progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentStatusDto.enrollmentId(), command.resourceId())
                .orElseGet(() -> Progress.of(enrollmentStatusDto.enrollmentId(), command.resourceId()));

        progress.updateProgress(command.watchedDuration(), lectureDurationDto.totalDurationSeconds());

        Progress savedProgress = progressRepository.save(progress);
        return ProgressUpdateResponse.from(savedProgress);
    }
}

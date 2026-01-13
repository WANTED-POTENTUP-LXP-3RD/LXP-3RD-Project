package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.progress.application.command.ProgressUpdateCommand;
import com.lxp.aplus.progress.application.port.LectureInfo;
import com.lxp.aplus.progress.application.port.LectureProvider;
import com.lxp.aplus.progress.presentation.response.ProgressUpdateResponse;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ProgressCommandUseCase {

    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureProvider lectureProvider;

    public ProgressUpdateResponse updateProgress(Long userId, Long courseId, ProgressUpdateCommand command) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        if (enrollment.isExpired(LocalDateTime.now())) {
            throw new BusinessException(ProgressErrorCode.CANNOT_UPDATE_EXPIRED_ENROLLMENT);
        }

        LectureInfo lectureInfo = lectureProvider.getLectureInfo(command.resourceId());
        if (lectureInfo == null) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
        }

        Progress progress = progressRepository.findByEnrollmentIdAndLectureResourceId(enrollment.getId(), command.resourceId())
                .orElseGet(() -> Progress.of(enrollment.getId(), command.resourceId()));

        progress.updateProgress(command.watchedDuration(), lectureInfo.totalDurationSeconds());

        Progress savedProgress = progressRepository.save(progress);
        return ProgressUpdateResponse.from(savedProgress);
    }
}

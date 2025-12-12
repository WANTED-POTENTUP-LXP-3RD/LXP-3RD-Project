package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import com.lxp.aplus.progress.presentation.request.ProgressUpdateRequest;
import com.lxp.aplus.progress.presentation.response.ProgressUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressCommandUseCase {

    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public ProgressUpdateResponse updateProgress(UserInfo currentUser, Long enrollmentId, ProgressUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        if (!enrollment.getStudentId().equals(currentUser.id())) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
        }

        Lecture parentLecture = courseRepository.findAllLecturesWithResourcesByCourseId(enrollment
                        .getCourseId()).stream()
                .filter(lecture -> lecture.getLectureResources().stream()
                        .anyMatch(resource -> resource.getId().equals(request.resourceId())))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ProgressErrorCode.LEARNING_HISTORY_NOT_FOUND));

        LectureResource lectureResource = parentLecture.getLectureResources().stream()
                .filter(resource -> resource.getId().equals(request.resourceId()))
                .findFirst()
                .get();

        Optional<Progress> existingProgress = progressRepository
                .findByEnrollmentAndLectureResource(enrollment, lectureResource);
        Progress progress;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            progress = Progress.of(enrollment, lectureResource);
        }
        
        int totalDuration = parentLecture.getTotalDurationSeconds();

        boolean wasAlreadyCompleted = progress.isCompleted();
        boolean isNowCompleted = request.watchedDuration() >= totalDuration;
        boolean finalCompletionStatus = wasAlreadyCompleted || isNowCompleted;

        progress.updateProgress(request.watchedDuration(), finalCompletionStatus);

        Progress savedProgress = progressRepository.save(progress);

        int currentProgressRate = savedProgress.calculateProgressRate();

        return ProgressUpdateResponse.of(savedProgress, currentProgressRate);
    }

    public ProgressUpdateResponse updateProgressByCourseId(UserInfo currentUser, Long courseId, ProgressUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(currentUser.id(), courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        return updateProgress(currentUser, enrollment.getId(), request);
    }
}

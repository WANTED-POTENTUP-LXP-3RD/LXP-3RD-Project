package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
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

    public ProgressUpdateResponse updateProgress(Long enrollmentId, ProgressUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        Lecture parentLecture = courseRepository.findAllLecturesWithResourcesByCourseId(enrollment.getCourseId()).stream()
                .filter(lecture -> lecture.getLectureResources().stream()
                        .anyMatch(resource -> resource.getId().equals(request.resourceId())))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ProgressErrorCode.LEARNING_HISTORY_NOT_FOUND));

        LectureResource lectureResource = parentLecture.getLectureResources().stream()
                .filter(resource -> resource.getId().equals(request.resourceId()))
                .findFirst()
                .get();

        Optional<Progress> existingProgress = progressRepository.findByEnrollmentAndLectureResource(enrollment, lectureResource);
        Progress progress;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            progress = Progress.builder()
                    .enrollment(enrollment)
                    .lectureResource(lectureResource)
                    .build();
        }
        
        int totalDuration = parentLecture.getTotalDurationSeconds();
        if (request.watchedDuration() > totalDuration) {
            throw new BusinessException(ProgressErrorCode.WATCHED_DURATION_EXCEEDS_TOTAL);
        }

        boolean isCompleted = request.watchedDuration() >= totalDuration;
        progress.updateProgress(request.watchedDuration(), isCompleted);

        Progress savedProgress = progressRepository.save(progress);

        int currentProgressRate = (int) (((double) savedProgress.getWatchedDuration() / totalDuration) * 100);

        return new ProgressUpdateResponse(
                savedProgress.getLectureResource().getId(),
                savedProgress.getEnrollment().getId(),
                currentProgressRate,
                savedProgress.getLectureResource().getId(),
                savedProgress.getWatchedDuration(),
                savedProgress.getLastWatchedAt()
        );
    }
}

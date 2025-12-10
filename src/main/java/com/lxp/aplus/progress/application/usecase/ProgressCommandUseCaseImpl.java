package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.progress.application.command.ProgressUpdateCommand;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import com.lxp.aplus.progress.presentation.response.ProgressUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressCommandUseCaseImpl implements ProgressCommandUseCase {

    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository; 

    @Override
    public ProgressUpdateResponse updateProgress(ProgressUpdateCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.enrollmentId())
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        if (enrollment.isExpired()) {
            throw new BusinessException(ProgressErrorCode.CANNOT_UPDATE_EXPIRED_ENROLLMENT);
        }

        AtomicReference<Lecture> parentLecture = new AtomicReference<>();
        LectureResource lectureResource = courseRepository.findAllLecturesWithResourcesByCourseId(enrollment.getCourseId()).stream()
                .filter(lecture -> {
                    boolean found = lecture.getLectureResources().stream()
                            .anyMatch(resource -> resource.getId().equals(command.resourceId()));
                    if (found) {
                        parentLecture.set(lecture);
                    }
                    return found;
                })
                .findFirst()
                .flatMap(lecture -> lecture.getLectureResources().stream()
                        .filter(resource -> resource.getId().equals(command.resourceId()))
                        .findFirst())
                .orElseThrow(() -> new BusinessException(ProgressErrorCode.LEARNING_HISTORY_NOT_FOUND));

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
        
        int totalDuration = parentLecture.get().getTotalDurationSeconds();
        if (command.watchedDuration() > totalDuration) {
            throw new BusinessException(ProgressErrorCode.WATCHED_DURATION_EXCEEDS_TOTAL);
        }

        boolean isCompleted = command.watchedDuration() >= totalDuration;
        progress.updateProgress(command.watchedDuration(), isCompleted);

        Progress savedProgress = progressRepository.save(progress);

        int currentProgressRate = (int) (((double) savedProgress.getWatchedDuration() / totalDuration) * 100);

        return ProgressUpdateResponse.of(
                savedProgress.getLectureResource().getId(),
                savedProgress.getEnrollment().getId(),
                currentProgressRate,
                savedProgress.getLectureResource().getId(),
                savedProgress.getWatchedDuration(),
                savedProgress.getLastWatchedAt()
        );
    }
}

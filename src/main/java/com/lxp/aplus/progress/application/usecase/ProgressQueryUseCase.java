package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.LectureResourceSummary;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.progress.application.result.LearningHistoryResult;
import com.lxp.aplus.progress.application.result.LectureProgressResult;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressQueryUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseFinder courseFinder;
    private final ProgressRepository progressRepository;
    private static final int PERCENTAGE_MULTIPLIER = 100;

    public LearningHistoryResult getLearningHistory(Long studentId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));
        enrollment.validateOwner(studentId);

        if (enrollment.isExpired()) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_EXPIRED_HISTORY_ACCESS_DENIED);
        }

        List<LectureResourceSummary> allLectureResources = courseFinder.findAllLectureResourcesByCourseId(enrollment.getCourseId());
        
        if (allLectureResources.isEmpty()) {
            throw new BusinessException(ProgressErrorCode.LEARNING_HISTORY_NOT_FOUND);
        }

        List<Long> allResourceIds = allLectureResources.stream()
                .map(LectureResourceSummary::resourceId)
                .collect(Collectors.toList());
        
        List<Progress> progresses = progressRepository.findByEnrollmentIdAndLectureResourceIds(enrollmentId, allResourceIds);
        
        if (progresses.isEmpty()) {
            throw new BusinessException(ProgressErrorCode.LEARNING_HISTORY_NOT_FOUND);
        }

        Map<Long, Progress> progressMap = progresses.stream()
                .collect(Collectors.toMap(p -> p.getLectureResource().getId(), p -> p)); 

        List<LectureProgressResult> lectureProgresses = allLectureResources.stream()
                .map(resourceSummary -> {
                    Progress progress = progressMap.get(resourceSummary.resourceId());
                    return LectureProgressResult.from(resourceSummary, progress); 
                })
                .collect(Collectors.toList());

        long completedCount = lectureProgresses.stream().filter(LectureProgressResult::isCompleted).count();
        int overallProgressRate = (int) ((double) completedCount / allResourceIds.size() * PERCENTAGE_MULTIPLIER);

        Optional<Progress> lastWatchedProgress = progresses.stream()
                .filter(p -> p.getLastWatchedAt() != null)
                .max(Comparator.comparing(Progress::getLastWatchedAt));
        
        Long lastWatchedVideoId = lastWatchedProgress.map(p -> p.getLectureResource().getId()).orElse(null);
        Integer lastWatchedDuration = lastWatchedProgress.map(Progress::getWatchedDuration).orElse(null);
        java.time.LocalDateTime lastWatchedAt = lastWatchedProgress.map(Progress::getLastWatchedAt).orElse(null);

        return new LearningHistoryResult(
                enrollmentId,
                overallProgressRate,
                lastWatchedVideoId,
                lastWatchedDuration,
                lastWatchedAt,
                lectureProgresses
        );
    }
}
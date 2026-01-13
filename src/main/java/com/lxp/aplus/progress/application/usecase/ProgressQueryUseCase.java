package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.progress.application.port.LectureDetailInfo;
import com.lxp.aplus.progress.application.port.LectureProvider;
import com.lxp.aplus.progress.presentation.response.CourseProgressResponse;
import com.lxp.aplus.progress.presentation.response.LectureProgressResponse;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProgressQueryUseCase {

    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureProvider lectureProvider;

    public CourseProgressResponse getCourseProgress(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        if (enrollment.isExpired(LocalDateTime.now())) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_EXPIRED_HISTORY_ACCESS_DENIED);
        }

        List<LectureDetailInfo> lectureDetails = lectureProvider.getLectureDetailsByCourseId(courseId);

        List<Progress> progresses = progressRepository.findByEnrollmentId(enrollment.getId());
        Map<Long, Progress> progressMap = progresses.stream()
                .collect(Collectors.toMap(Progress::getLectureResourceId, Function.identity()));

        List<LectureProgressResponse> lectureProgressResponses = lectureDetails.stream()
                .map(lecture -> {
                    Progress progress = progressMap.get(lecture.resourceId());
                    return new LectureProgressResponse(
                            lecture.resourceId(),
                            lecture.title(),
                            progress != null ? progress.getWatchedDuration() : 0,
                            lecture.totalDurationSeconds(),
                            progress != null && progress.isCompleted(),
                            progress != null ? progress.getLastWatchedAt() : null
                    );
                })
                .toList();

        int overallProgressRate = calculateOverallProgress(lectureProgressResponses);

        Progress lastWatchedProgress = progresses.stream()
                .filter(p -> p.getLastWatchedAt() != null)
                .max(Comparator.comparing(Progress::getLastWatchedAt))
                .orElse(null);

        return new CourseProgressResponse(
                enrollment.getId(),
                overallProgressRate,
                lastWatchedProgress != null ? lastWatchedProgress.getLectureResourceId() : null,
                lastWatchedProgress != null ? lastWatchedProgress.getLastWatchedAt() : null,
                lectureProgressResponses
        );
    }

    private int calculateOverallProgress(List<LectureProgressResponse> lectureProgresses) {
        if (lectureProgresses.isEmpty()) {
            return 0;
        }
        long totalCompleted = lectureProgresses.stream().filter(LectureProgressResponse::isCompleted).count();
        return (int) (totalCompleted * 100 / lectureProgresses.size());
    }
}

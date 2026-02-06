package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.progress.application.port.EnrollmentReader;
import com.lxp.aplus.progress.application.port.EnrollmentStatusDto;
import com.lxp.aplus.progress.application.port.LectureProvider;
import com.lxp.aplus.progress.application.port.LectureSummaryDto;
import com.lxp.aplus.progress.domain.LearningProgress;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import com.lxp.aplus.progress.presentation.response.CourseProgressResponse;
import com.lxp.aplus.progress.presentation.response.LectureProgressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProgressQueryUseCase {

    private final ProgressRepository progressRepository;
    private final EnrollmentReader enrollmentReader;
    private final LectureProvider lectureProvider;

    public CourseProgressResponse getCourseProgress(Long userId, Long courseId) {
        EnrollmentStatusDto enrollmentStatusDto = enrollmentReader.findEnrollment(userId, courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        if (enrollmentStatusDto.isExpired()) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_EXPIRED_HISTORY_ACCESS_DENIED);
        }

        List<Progress> progresses = progressRepository.findByEnrollmentId(enrollmentStatusDto.enrollmentId());
        List<LectureSummaryDto> lectureDetails = lectureProvider.getLectureDetailsByCourseId(courseId);

        LearningProgress learningProgress = new LearningProgress(enrollmentStatusDto.enrollmentId(), progresses);
        int overallProgressRate = learningProgress.calculateOverallProgressRate(lectureDetails);
        Optional<Progress> lastWatchedProgressOpt = learningProgress.findLastWatchedProgress();
        List<LectureProgressResponse> lectureProgressResponses = learningProgress.mapToLectureProgressResponses(lectureDetails);

        return new CourseProgressResponse(
                enrollmentStatusDto.enrollmentId(),
                overallProgressRate,
                lastWatchedProgressOpt.map(Progress::getLectureResourceId).orElse(null),
                lastWatchedProgressOpt.map(Progress::getLastWatchedAt).orElse(null),
                lectureProgressResponses
        );
    }
}
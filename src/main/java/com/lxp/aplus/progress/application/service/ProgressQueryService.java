package com.lxp.aplus.progress.application.service;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.progress.application.dto.response.CourseProgressResponse;
import com.lxp.aplus.progress.application.dto.response.ResourceProgressResponse;
import com.lxp.aplus.progress.application.port.in.ProgressQueryUseCase;
import com.lxp.aplus.progress.application.port.out.EnrollmentReader;
import com.lxp.aplus.progress.application.port.out.LectureQueryPort;
import com.lxp.aplus.progress.application.port.out.dto.EnrollmentStatusDto;
import com.lxp.aplus.progress.application.port.out.dto.LectureSummaryDto;
import com.lxp.aplus.progress.domain.LearningProgress;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.application.port.out.ProgressRepository;
import com.lxp.aplus.progress.domain.vo.LectureSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProgressQueryService implements ProgressQueryUseCase {

    private final ProgressRepository progressRepository;
    private final EnrollmentReader enrollmentReader;
    private final LectureQueryPort lectureProvider;

    @Override
    public CourseProgressResponse getCourseProgress(Long userId, Long courseId) {
        EnrollmentStatusDto enrollmentStatusDto = enrollmentReader.findEnrollment(userId, courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));

        if (enrollmentStatusDto.isExpired()) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_EXPIRED_HISTORY_ACCESS_DENIED);
        }

        List<Progress> progresses = progressRepository.findByEnrollmentId(enrollmentStatusDto.enrollmentId());
        List<LectureSummaryDto> lectureDetails = lectureProvider.getLectureDetailsByCourseId(courseId);
        List<LectureSummary> lectureSummaries = toLectureSummaries(lectureDetails);

        LearningProgress learningProgress = new LearningProgress(enrollmentStatusDto.enrollmentId(), progresses);
        int overallProgressRate = learningProgress.calculateOverallProgressRate(lectureSummaries);
        Optional<Progress> lastWatchedProgressOpt = learningProgress.findLastWatchedProgress();
        var progressByResourceId = learningProgress.mapProgressByResourceId();
        List<ResourceProgressResponse> resourceProgressResponses = lectureSummaries.stream()
                .map(lecture -> {
                    Progress progress = progressByResourceId.get(lecture.resourceId());
                    if (progress == null) {
                        return new ResourceProgressResponse(
                                lecture.resourceId(),
                                lecture.title(),
                                0,
                                lecture.totalDurationSeconds(),
                                false,
                                null
                        );
                    }
                    return new ResourceProgressResponse(
                            lecture.resourceId(),
                            lecture.title(),
                            progress.getWatchedDuration(),
                            lecture.totalDurationSeconds(),
                            progress.isCompleted(),
                            progress.getLastWatchedAt()
                    );
                })
                .toList();

        return new CourseProgressResponse(
                enrollmentStatusDto.enrollmentId(),
                overallProgressRate,
                lastWatchedProgressOpt.map(Progress::getLectureResourceId).orElse(null),
                lastWatchedProgressOpt.map(Progress::getLastWatchedAt).orElse(null),
                resourceProgressResponses
        );
    }

    private List<LectureSummary> toLectureSummaries(List<LectureSummaryDto> lectureDetails) {
        return lectureDetails.stream()
                .map(lecture -> new LectureSummary(
                        lecture.resourceId(),
                        lecture.title(),
                        lecture.resourceType(),
                        lecture.totalDurationSeconds()
                ))
                .toList();
    }
}

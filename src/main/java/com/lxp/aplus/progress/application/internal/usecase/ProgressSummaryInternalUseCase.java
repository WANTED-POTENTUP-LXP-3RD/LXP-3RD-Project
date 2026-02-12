package com.lxp.aplus.progress.application.internal.usecase;

import com.lxp.aplus.progress.application.internal.dto.ProgressSummaryInternalResult;
import com.lxp.aplus.progress.application.port.out.LectureQueryPort;
import com.lxp.aplus.progress.application.port.out.dto.LectureSummaryDto;
import com.lxp.aplus.progress.domain.LearningProgress;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.application.port.out.ProgressRepository;
import com.lxp.aplus.progress.domain.vo.LectureSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressSummaryInternalUseCase {

    private final ProgressRepository progressRepository;
    private final LectureQueryPort lectureProvider;

    @Transactional(readOnly = true)
    public ProgressSummaryInternalResult getOverallProgressRate(Long enrollmentId, Long courseId) {
        List<Progress> progresses = progressRepository.findByEnrollmentId(enrollmentId);
        if (progresses.isEmpty()) {
            return new ProgressSummaryInternalResult(0);
        }

        List<LectureSummaryDto> resources = lectureProvider.getLectureDetailsByCourseId(courseId);
        if (resources.isEmpty()) {
            return new ProgressSummaryInternalResult(0);
        }

        List<LectureSummary> summaries = toLectureSummaries(resources);
        LearningProgress learningProgress = new LearningProgress(enrollmentId, progresses);
        return new ProgressSummaryInternalResult(learningProgress.calculateOverallProgressRate(summaries));
    }

    @Transactional(readOnly = true)
    public boolean hasProgress(Long enrollmentId) {
        return progressRepository.existsByEnrollmentId(enrollmentId);
    }

    @Transactional
    public void removeByEnrollmentId(Long enrollmentId) {
        progressRepository.deleteByEnrollmentId(enrollmentId);
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

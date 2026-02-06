package com.lxp.aplus.progress.application.service.internal;

import com.lxp.aplus.progress.application.port.LectureSummaryDto;
import com.lxp.aplus.progress.application.port.in.external.ProgressSummaryPort;
import com.lxp.aplus.progress.application.port.LectureProvider;
import com.lxp.aplus.progress.domain.LearningProgress;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class ProgressSummaryService implements ProgressSummaryPort {

    private final ProgressRepository progressRepository;
    private final LectureProvider lectureProvider;

    @Override
    @Transactional(readOnly = true)
    public int getOverallProgressRate(Long enrollmentId, Long courseId) {
        List<Progress> progresses = progressRepository.findByEnrollmentId(enrollmentId);
        if (progresses.isEmpty()) {
            return 0;
        }

        List<LectureSummaryDto> resources = lectureProvider.getLectureDetailsByCourseId(courseId);
        if (resources.isEmpty()) {
            return 0;
        }

        LearningProgress learningProgress = new LearningProgress(enrollmentId, progresses);
        return learningProgress.calculateOverallProgressRate(resources);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasProgress(Long enrollmentId) {
        return progressRepository.existsByEnrollmentId(enrollmentId);
    }

    @Override
    @Transactional
    public void removeByEnrollmentId(Long enrollmentId) {
        progressRepository.deleteByEnrollmentId(enrollmentId);
    }
}

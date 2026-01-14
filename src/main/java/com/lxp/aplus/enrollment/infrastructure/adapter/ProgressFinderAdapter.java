package com.lxp.aplus.enrollment.infrastructure.adapter;

import com.lxp.aplus.enrollment.application.port.out.ProgressFinder;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProgressFinderAdapter implements ProgressFinder {

    private final ProgressRepository progressRepository;

    @Override
    public Map<Long, Boolean> getCompletionStatusMap(Long enrollmentId, List<Long> lectureResourceIds) {
        List<Progress> allProgressesForEnrollment = progressRepository.findByEnrollmentId(enrollmentId);

        return allProgressesForEnrollment.stream()
                .filter(progress -> lectureResourceIds.contains(progress.getLectureResourceId()))
                .collect(Collectors.toMap(
                        Progress::getLectureResourceId,
                        Progress::isCompleted
                ));
    }
}
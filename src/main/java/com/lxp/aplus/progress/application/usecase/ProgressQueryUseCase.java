package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressQueryUseCase {

    private final ProgressRepository progressRepository;

    public Map<Long, Boolean> checkLectureCompletionStatus(Long enrollmentId, List<Long> lectureResourceIds) {
        List<Progress> progresses = progressRepository.findByEnrollmentIdAndLectureResourceIds(enrollmentId, lectureResourceIds);

        return progresses.stream()
                .collect(Collectors.toMap(
                        progress -> progress.getLectureResource().getId(),
                        Progress::isCompleted
                ));
    }
}
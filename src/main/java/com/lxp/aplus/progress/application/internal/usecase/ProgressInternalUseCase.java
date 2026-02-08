package com.lxp.aplus.progress.application.internal.usecase;

import com.lxp.aplus.progress.application.internal.dto.ProgressInternalResult;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressInternalUseCase {
    private final ProgressRepository progressRepository;

    public Optional<ProgressInternalResult> findByEnrollmentIdAndResourceId(Long enrollmentId, Long lectureResourceId) {
        return progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, lectureResourceId)
                .map(ProgressInternalResult::from);
    }

    public List<ProgressInternalResult> findAllByEnrollmentId(Long enrollmentId) {
        return progressRepository.findByEnrollmentId(enrollmentId)
                .stream()
                .map(ProgressInternalResult::from)
                .toList();
    }
}

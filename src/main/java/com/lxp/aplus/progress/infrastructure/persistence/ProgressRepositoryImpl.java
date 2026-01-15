package com.lxp.aplus.progress.infrastructure.persistence;

import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProgressRepositoryImpl implements ProgressRepository {

    private final ProgressJpaRepository jpaRepository;

    @Override
    public Progress save(Progress progress) {
        return jpaRepository.save(progress);
    }

    @Override
    public Optional<Progress> findByEnrollmentIdAndLectureResourceId(Long enrollmentId, Long lectureResourceId) {
        return jpaRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, lectureResourceId);
    }

    @Override
    public List<Progress> findByEnrollmentId(Long enrollmentId) {
        return jpaRepository.findByEnrollmentId(enrollmentId);
    }

    @Override
    public boolean existsByEnrollmentId(Long enrollmentId) {
        return jpaRepository.existsByEnrollmentId(enrollmentId);
    }

    @Override
    public void deleteByEnrollmentId(Long enrollmentId) {
        jpaRepository.deleteByEnrollmentId(enrollmentId);
    }
}

package com.lxp.aplus.progress.infrastructure.persistence;

import com.lxp.aplus.progress.domain.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressJpaRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByEnrollmentIdAndLectureResourceId(Long enrollmentId, Long lectureResourceId);

    List<Progress> findByEnrollmentId(Long enrollmentId);

    boolean existsByEnrollmentId(Long enrollmentId);

    void deleteByEnrollmentId(Long enrollmentId);
}
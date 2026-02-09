package com.lxp.aplus.progress.application.port.out;

import com.lxp.aplus.progress.domain.Progress;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository {
    Progress save(Progress progress);

    Optional<Progress> findByEnrollmentIdAndLectureResourceId(Long enrollmentId, Long lectureResourceId);

    List<Progress> findByEnrollmentId(Long enrollmentId);

    boolean existsByEnrollmentId(Long enrollmentId);

    void deleteByEnrollmentId(Long enrollmentId);
}

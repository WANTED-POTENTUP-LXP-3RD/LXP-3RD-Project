package com.lxp.aplus.progress.domain;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository {
    Progress save(Progress progress);

    Optional<Progress> findByEnrollmentIdAndLectureResourceId(Long enrollmentId, Long lectureResourceId);

    List<Progress> findByEnrollmentId(Long enrollmentId);
}

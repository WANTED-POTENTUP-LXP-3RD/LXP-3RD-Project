package com.lxp.aplus.progress.domain;

import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.enrollment.domain.Enrollment;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProgressRepository {
    Progress save(Progress progress);
    Optional<Progress> findByEnrollmentAndLectureResource(Enrollment enrollment, LectureResource lectureResource);
    List<Progress> findByEnrollmentId(Long enrollmentId);
    long countByEnrollmentIdAndIsCompleted(Long enrollmentId, boolean isCompleted);
    List<Progress> findByEnrollmentIdAndLectureResourceIds(Long enrollmentId, List<Long> lectureResourceIds);
    Map<Long, Boolean> getCompletionStatusMap(Long enrollmentId, List<Long> lectureResourceIds);
}

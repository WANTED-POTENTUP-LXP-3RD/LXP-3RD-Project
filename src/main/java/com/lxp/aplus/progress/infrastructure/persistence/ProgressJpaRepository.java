package com.lxp.aplus.progress.infrastructure.persistence;

import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.progress.domain.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProgressJpaRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByEnrollmentAndLectureResource(Enrollment enrollment, LectureResource lectureResource);
    List<Progress> findByEnrollmentId(Long enrollmentId);
    long countByEnrollmentIdAndIsCompleted(Long enrollmentId, boolean isCompleted);

    @Query("SELECT p FROM Progress p JOIN FETCH p.lectureResource lr WHERE p.enrollment.id = :enrollmentId AND lr.id IN :lectureResourceIds")
    List<Progress> findByEnrollment_IdAndLectureResource_IdIn(@Param("enrollmentId") Long enrollmentId, @Param("lectureResourceIds") List<Long> lectureResourceIds);
}

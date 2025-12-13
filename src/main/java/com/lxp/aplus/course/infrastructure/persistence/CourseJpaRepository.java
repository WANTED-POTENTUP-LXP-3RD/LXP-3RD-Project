package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseStatus;
import com.lxp.aplus.course.domain.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseJpaRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByIdAndCourseStatusNot(Long id, CourseStatus courseStatus);

    Page<Course> findAllByInstructorIdAndCourseStatusNot(Long instructorId, CourseStatus courseStatus, Pageable pageable);

    Page<Course> findAllByCourseStatus(CourseStatus courseStatus, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.sections s " +
            "WHERE c.id = :courseId")
    Optional<Course> findWithCurriculumById(@Param("courseId") Long courseId);

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.sections s " +
            "WHERE c.id = :courseId AND c.courseStatus = 'PUBLISHED'")
    Optional<Course> findPublishedWithCurriculumById(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(l) FROM Lecture l " +
            "INNER JOIN l.section s " +
            "WHERE s.course.id = :courseId")
    int countLecturesByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT DISTINCT l FROM Lecture l " +
            "LEFT JOIN FETCH l.lectureResources lr " +
            "INNER JOIN l.section s " +
            "WHERE s.course.id = :courseId")
    List<Lecture> findAllLecturesWithResourcesByCourseId(@Param("courseId") Long courseId);

    List<Course> findByIdIn(List<Long> ids);
}

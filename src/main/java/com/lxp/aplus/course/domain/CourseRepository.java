package com.lxp.aplus.course.domain;

import com.lxp.aplus.course.application.port.in.dto.ResourceSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);
    void flush();
    Optional<Course> findById(Long id);
    Optional<Course> findWithCurriculumById(Long courseId);
    Optional<Course> findPublishedWithCurriculumById(Long courseId);
    Page<Course> findAllByInstructorIdExcludingDeleted(Long instructorId, Pageable pageable);
    Page<Course> findAllPublished(Pageable pageable);
    int countLecturesByCourseId(Long courseId);
    List<Lecture> findAllLecturesWithResourcesByCourseId(Long courseId);
    List<Course> findByIdIn(List<Long> ids);
    List<ResourceSummary> findLectureSummariesByCourseId(Long courseId);
}

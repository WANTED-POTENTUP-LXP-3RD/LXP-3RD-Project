package com.lxp.aplus.course.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);
    void flush();
    Optional<Course> findById(Long id);
    Page<Course> findAllByInstructorId(Long instructorId, Pageable pageable);
    Page<Course> findAllByPublished(Pageable pageable);
    Optional<Course> findWithCurriculumById(Long courseId);
    Optional<Course> findPublishedWithCurriculumById(Long courseId);
    int countLecturesByCourseId(Long courseId);
    List<Lecture> findAllLecturesWithResourcesByCourseId(Long courseId);
}

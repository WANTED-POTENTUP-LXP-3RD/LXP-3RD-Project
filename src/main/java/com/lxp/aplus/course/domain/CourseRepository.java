package com.lxp.aplus.course.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);
    Optional<Course> findById(Long id);
    Page<Course> findAllByInstructorId(Long instructorId, Pageable pageable);
    Page<Course> findAllByPublished(Pageable pageable);
}

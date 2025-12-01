package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseJpaRepository extends JpaRepository<Course, Long> {
}

package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {
    private final CourseJpaRepository jpaRepository;

    @Override
    public Course save(Course course) {
        return jpaRepository.save(course);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return jpaRepository.findById(id);
    }
}

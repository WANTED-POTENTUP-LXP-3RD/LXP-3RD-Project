package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<Course> findAllByInstructorId(Long instructorId, Pageable pageable) {
        return jpaRepository.findAllByInstructorId(instructorId, pageable);
    }

    @Override
    public Page<Course> findAllByPublished(Pageable pageable) {
        return jpaRepository.findAllByCourseStatus(CourseStatus.PUBLISHED, pageable);
    }
}

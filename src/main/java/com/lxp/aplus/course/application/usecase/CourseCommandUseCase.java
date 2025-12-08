package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.application.command.CourseCreateCommand;
import com.lxp.aplus.course.application.command.CourseUpdateCommand;
import com.lxp.aplus.course.application.result.CourseResult;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseCommandUseCase {
    private final CourseRepository courseRepository;

    public CourseResult createCourse(Long instructorId, CourseCreateCommand command) {
        Course course = Course.createDraftCourse(instructorId, command);
        Course savedCourse = courseRepository.save(course);

        return CourseResult.from(savedCourse);
    }

    public CourseResult updateCourse(Long courseId, Long instructorId, CourseUpdateCommand command) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(instructorId);
        course.updateCourseInfo(command);

        return CourseResult.from(course);
    }
}

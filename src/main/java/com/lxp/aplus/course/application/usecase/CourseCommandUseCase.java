package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.file.FileUploader;
import com.lxp.aplus.course.application.command.CourseCreateCommand;
import com.lxp.aplus.course.application.command.CourseUpdateCommand;
import com.lxp.aplus.course.application.result.CoursePublishResult;
import com.lxp.aplus.course.application.result.CourseUpsertResult;
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

    public CourseUpsertResult createCourse(CourseCreateCommand command) {
        Course course = Course.createDraftCourse(command);
        Course savedCourse = courseRepository.save(course);

        return CourseUpsertResult.from(savedCourse);
    }

    public CourseUpsertResult updateCourse(CourseUpdateCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(command.instructorId());
        course.updateCourse(command);

        return CourseUpsertResult.from(course);
    }

    public void deleteCourse(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(instructorId);
        course.deleteCourse();
    }

    public CoursePublishResult publishCourse(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(instructorId);
        course.publish();

        return CoursePublishResult.from(course);
    }
}

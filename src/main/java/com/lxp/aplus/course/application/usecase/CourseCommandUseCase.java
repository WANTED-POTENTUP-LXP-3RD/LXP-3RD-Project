package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.SectionErrorCode;
import com.lxp.aplus.course.application.command.CourseCreateCommand;
import com.lxp.aplus.course.application.command.CourseUpdateCommand;
import com.lxp.aplus.course.application.command.SectionCreateCommand;
import com.lxp.aplus.course.application.command.SectionUpdateCommand;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.Section;
import com.lxp.aplus.course.presentation.response.CourseUpsertResponse;
import com.lxp.aplus.course.presentation.response.SectionUpsertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseCommandUseCase {
    private final CourseRepository courseRepository;

    public CourseUpsertResponse createCourse(Long instructorId, CourseCreateCommand command) {
        Course course = Course.createDraftCourse(instructorId, command);
        Course savedCourse = courseRepository.save(course);

        return CourseUpsertResponse.from(savedCourse);
    }

    public CourseUpsertResponse updateCourse(Long courseId, Long instructorId, CourseUpdateCommand command) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(instructorId);
        course.updateCourseInfo(command);

        return CourseUpsertResponse.from(course);
    }

    public SectionUpsertResponse createSection(Long courseId, Long instructorId, SectionCreateCommand command) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(instructorId);
        course.addSection(command.title(), command.orderIndex());
        Course savedCourse = courseRepository.save(course);
        courseRepository.flush();

        Section newSection = savedCourse.getSections().stream()
                .filter(section -> section.getOrderIndex() == command.orderIndex() 
                        && section.getTitle().equals(command.title()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(SectionErrorCode.SECTION_NOT_FOUND));

        return SectionUpsertResponse.from(newSection);
    }

    public SectionUpsertResponse updateSection(Long courseId, Long instructorId, Long sectionId, SectionUpdateCommand command) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(instructorId);
        Section updatedSection = course.updateSection(sectionId, command.title(), command.orderIndex());

        return SectionUpsertResponse.from(updatedSection);
    }

    public void deleteSection(Long courseId, Long instructorId, Long sectionId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(instructorId);
        course.deleteSection(sectionId);
    }

    public void deleteCourse(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(instructorId);
        course.delete();
    }
}

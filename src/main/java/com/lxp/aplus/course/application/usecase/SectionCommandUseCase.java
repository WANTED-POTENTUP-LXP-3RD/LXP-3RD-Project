package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.SectionErrorCode;
import com.lxp.aplus.course.application.command.SectionCreateCommand;
import com.lxp.aplus.course.application.command.SectionDeleteCommand;
import com.lxp.aplus.course.application.command.SectionUpdateCommand;
import com.lxp.aplus.course.application.result.SectionUpsertResult;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SectionCommandUseCase {
    private final CourseRepository courseRepository;

    public SectionUpsertResult createSection(SectionCreateCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(command.instructorId());
        course.addSection(command.title(), command.orderIndex());
        Course savedCourse = courseRepository.save(course);
        courseRepository.flush();

        Section newSection = savedCourse.getSections().stream()
                .filter(section -> section.getOrderIndex() == command.orderIndex()
                        && section.getTitle().equals(command.title()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(SectionErrorCode.SECTION_NOT_FOUND));

        return SectionUpsertResult.from(newSection);
    }

    public SectionUpsertResult updateSection(SectionUpdateCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(command.instructorId());
        Section updatedSection = course.updateSection(command.sectionId(), command.title(), command.orderIndex());

        return SectionUpsertResult.from(updatedSection);
    }

    public void deleteSection(SectionDeleteCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(command.instructorId());
        course.deleteSection(command.sectionId());
    }
}

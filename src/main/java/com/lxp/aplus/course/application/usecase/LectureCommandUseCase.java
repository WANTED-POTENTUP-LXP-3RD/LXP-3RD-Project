package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.application.command.CreateLectureCommand;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.Lecture;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureCommandUseCase {
    
    private final CourseRepository courseRepository;
    
    public LectureResult createLecture(Long courseId, Long sectionId, CreateLectureCommand command) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        Lecture lecture = course.addLectureToSection(
                sectionId,
                command.title(),
                command.description()
        );

        courseRepository.save(course);

        return LectureResult.from(lecture);
    }
}

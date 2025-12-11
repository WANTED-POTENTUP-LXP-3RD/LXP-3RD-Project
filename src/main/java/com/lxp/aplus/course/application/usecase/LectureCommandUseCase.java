package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.application.command.CreateLectureCommand;
import com.lxp.aplus.course.application.command.UpdateLectureCommand;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.domain.*;
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

        Lecture lecture = course.createLecture(
                sectionId,
                command.title(),
                command.totalDurationSeconds(),
                command.isPreview(),
                command.orderIndex(),
                command.resource().isDownloadable()
        );

        courseRepository.save(course);
        return LectureResult.from(lecture);
    }

    public LectureResult updateLecture(Long courseId, Long lectureId, UpdateLectureCommand command) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        Lecture lecture = course.updateLecture(
                lectureId,
                command.title(),
                command.totalDurationSeconds(),
                command.isPreview(),
                command.orderIndex(),
                command.resource().isDownloadable()
        );

        courseRepository.save(course);
        return LectureResult.from(lecture);
    }

    public void deleteLecture(Long courseId, Long lectureId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.deleteLecture(lectureId);
    }
}

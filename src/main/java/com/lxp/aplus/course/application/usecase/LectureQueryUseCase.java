package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.course.application.mapper.LectureResultUrlMapper;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.Lecture;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.lxp.aplus.common.error.code.CourseErrorCode.COURSE_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureQueryUseCase {
    private final LectureResultUrlMapper lectureResultUrlMapper;
    private final CourseRepository courseRepository;

    public LectureResult getLectureDetails (Long courseId, Long lectureId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(COURSE_NOT_FOUND));
        Lecture lecture = course.readLecture(lectureId);

        return lectureResultUrlMapper.toResult(lecture);
    }
}

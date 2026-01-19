package com.lxp.aplus.course.application.usecase.external;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import com.lxp.aplus.course.application.port.in.dto.ResourceDuration;
import com.lxp.aplus.course.application.port.in.dto.ResourceSummary;
import com.lxp.aplus.course.application.port.in.CourseQueryToProgressUseCase;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.infrastructure.persistence.LectureResourceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseQueryToProgressUseCaseImpl implements CourseQueryToProgressUseCase {
    private final LectureResourceJpaRepository lectureResourceJpaRepository;
    private final CourseRepository courseRepository;

    @Override
    public ResourceDuration getLectureDuration(Long resourceId) {
        return lectureResourceJpaRepository.findById(resourceId)
                .map(ResourceDuration::from).orElseThrow(() -> new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_VIDEO_DURATION_NOT_FOUND));

    }

    @Override
    public List<ResourceSummary> findLectureSummariesByCourseId(Long courseId) {
        return courseRepository.findLectureSummariesByCourseId(courseId);
    }
}

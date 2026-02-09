package com.lxp.aplus.progress.infrastructure.adapter.module;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.course.application.port.in.CourseQueryToProgressUseCase;
import com.lxp.aplus.course.application.port.in.dto.ResourceSummary;
import com.lxp.aplus.progress.application.port.out.LectureQueryPort;
import com.lxp.aplus.progress.application.port.out.dto.LectureDurationDto;
import com.lxp.aplus.progress.application.port.out.dto.LectureSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProgressLectureModuleAdapter implements LectureQueryPort {

    private final CourseQueryToProgressUseCase courseQueryToProgressUseCase;

    @Override
    public LectureDurationDto getLectureInfo(Long lectureResourceId) {
        try {
            return new LectureDurationDto(
                    courseQueryToProgressUseCase.getLectureDuration(lectureResourceId).totalDurationSeconds()
            );
        } catch (BusinessException e) {
            return null;
        }
    }

    @Override
    public List<LectureSummaryDto> getLectureDetailsByCourseId(Long courseId) {
        List<ResourceSummary> resourceSummaries = courseQueryToProgressUseCase.findLectureSummariesByCourseId(courseId);
        return resourceSummaries.stream()
                .map(LectureSummaryDto::from)
                .collect(Collectors.toList());
    }
}

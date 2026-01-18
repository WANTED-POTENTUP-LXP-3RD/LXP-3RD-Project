package com.lxp.aplus.progress.infrastructure.adapter;

import com.lxp.aplus.course.application.port.in.CourseQueryToProgressUseCase;
import com.lxp.aplus.progress.application.port.LectureSummaryDto;
import com.lxp.aplus.progress.application.port.LectureDurationDto;
import com.lxp.aplus.progress.application.port.LectureProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LectureProviderPlaceholder implements LectureProvider {
    private final CourseQueryToProgressUseCase courseQueryToProgressUseCase;

    @Override
    public LectureDurationDto getLectureInfo(Long resourceId) {
        return new LectureDurationDto(courseQueryToProgressUseCase.getLectureDuration(resourceId).totalDurationSeconds());
    }


    @Override
    public List<LectureSummaryDto> getLectureDetailsByCourseId(Long courseId) {
        return courseQueryToProgressUseCase.findLectureSummariesByCourseId(courseId).stream().map(LectureSummaryDto::from).toList();
    }
}

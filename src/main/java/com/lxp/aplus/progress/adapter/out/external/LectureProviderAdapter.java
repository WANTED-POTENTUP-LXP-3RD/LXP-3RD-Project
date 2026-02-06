package com.lxp.aplus.progress.adapter.out.external;

import com.lxp.aplus.course.application.port.in.dto.ResourceSummary;
import com.lxp.aplus.course.infrastructure.persistence.CourseJpaRepository;
import com.lxp.aplus.progress.application.port.LectureDurationDto;
import com.lxp.aplus.progress.application.port.LectureProvider;
import com.lxp.aplus.progress.application.port.LectureSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class LectureProviderAdapter implements LectureProvider {

    private final CourseJpaRepository courseJpaRepository;

    @Override
    public LectureDurationDto getLectureInfo(Long lectureResourceId) {
        return courseJpaRepository.findLectureDurationByResourceId(lectureResourceId)
                .map(LectureDurationDto::new)
                .orElse(null);
    }

    @Override
    public List<LectureSummaryDto> getLectureDetailsByCourseId(Long courseId) {
        List<ResourceSummary> resourceSummaries = courseJpaRepository.findLectureSummariesByCourseId(courseId);
        return resourceSummaries.stream()
                .map(LectureSummaryDto::from)
                .collect(Collectors.toList());
    }
}

package com.lxp.aplus.course.application.mapper;

import com.lxp.aplus.course.application.result.SectionResult;
import com.lxp.aplus.course.domain.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SectionResultMapper {
    private final LectureResultUrlMapper lectureResultUrlMapper;

    public SectionResult toResult(Section section) {
        return SectionResult.builder()
                .sectionId(section.getId())
                .title(section.getTitle())
                .order(section.getOrderIndex())
                .lectures(lectureResultUrlMapper.toResults(section.getLectures()))
                .build();
    }

    public List<SectionResult> toResults(List<Section> sections) {
        return sections.stream()
                .map(this::toResult)
                .toList();
    }
}

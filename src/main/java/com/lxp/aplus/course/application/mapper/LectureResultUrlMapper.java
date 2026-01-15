package com.lxp.aplus.course.application.mapper;

import com.lxp.aplus.course.application.port.out.PresignedUrlGenerator;
import com.lxp.aplus.course.application.result.LectureResourceResult;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.domain.LectureResourceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LectureResultUrlMapper {
    private final PresignedUrlGenerator presignedUrlGenerator;

    public LectureResourceResult toResult(LectureResourceV2 resource) {
        String url = presignedUrlGenerator.generateGetUrl(resource.getFileKey()).url();
        return LectureResourceResult.builder()
                .resourceType(resource.getResourceType())
                .isDownloadable(resource.isDownloadable())
                .fileUrl(url)
                .build();
    }

    public LectureResult toResult(Lecture lecture) {
        LectureResourceResult resourceResult = Optional.ofNullable(lecture.getLectureResources())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(this::toResult)
                .orElse(null);

        return LectureResult.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .totalDurationSeconds(lecture.getTotalDurationSeconds())
                .isPreview(lecture.isPreview())
                .orderIndex(lecture.getOrderIndex())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .resource(resourceResult)
                .build();
    }

    public List<LectureResult> toResults(List<Lecture> lectures) {
        return lectures.stream()
                .map(this::toResult)
                .toList();
    }
}

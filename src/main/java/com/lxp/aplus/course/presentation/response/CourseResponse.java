package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.dto.ReviewStat;
import com.lxp.aplus.course.application.result.CourseResult;
import com.lxp.aplus.course.domain.CourseLevel;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CourseResponse(
        Long courseId,
        String title,
        String summary,
        String instructorName,
        List<String> categories,
        String thumbnailUrl,
        CourseStatus status,
        int price,
        CourseLevel level,
        int studentCount,
        ReviewStat reviewStat,
        LocalDateTime lastModifiedAt
) {
    public static CourseResponse from(CourseResult result) {
        return CourseResponse.builder()
                .courseId(result.courseId())
                .title(result.title())
                .summary(result.summary())
                .instructorName(result.instructorName())
                .categories(result.categories())
                .thumbnailUrl(result.thumbnailUrl())
                .status(result.status())
                .price(result.price())
                .level(result.level())
                .studentCount(result.studentCount())
                .reviewInfo(result.reviewStat())
                .lastModifiedAt(result.lastModifiedAt())
                .build();
    }
}

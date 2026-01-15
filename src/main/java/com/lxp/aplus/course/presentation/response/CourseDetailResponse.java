package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.dto.ReviewStat;
import com.lxp.aplus.course.application.result.CourseDetailResult;
import com.lxp.aplus.course.domain.CourseLevel;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record CourseDetailResponse(
        Long courseId,
        List<String> categories,
        String title,
        String summary,
        String description,
        ReviewStat reviewStat,
        int price,
        CourseStatus status,
        CourseLevel level,
        String thumbnailUrl,
        InstructorResponse instructor,
        boolean isPurchased,
        int studentCount,
        int totalDuration,
        List<SectionResponse> sections
) {
    public static CourseDetailResponse from(CourseDetailResult result) {
        return CourseDetailResponse.builder()
                .courseId(result.courseId())
                .categories(result.categories())
                .title(result.title())
                .summary(result.summary())
                .description(result.description())
                .reviewInfo(result.reviewStat())
                .price(result.price())
                .status(result.status())
                .level(result.level())
                .thumbnailUrl(result.thumbnailUrl())
                .instructor(InstructorResponse.from(result.instructor()))
                .isPurchased(result.isPurchased())
                .studentCount(result.studentCount())
                .totalDuration(result.totalDuration())
                .sections(result.sections().stream()
                        .map(SectionResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}

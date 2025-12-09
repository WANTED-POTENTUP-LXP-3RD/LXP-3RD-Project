package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CourseResponse(
        Long courseId,
        String title,
        List<String> categories,
        String thumbnailUrl,
        CourseStatus status,
        int price,
        int studentCount,
        double rating,
        LocalDateTime lastModifiedAt
) {
    public static CourseResponse of(Course course, List<String> categoryNames) {
        return CourseResponse.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .categories(categoryNames)
                .thumbnailUrl(course.getThumbnailUrl())
                .status(course.getCourseStatus())
                .price(course.getPrice())
                .studentCount(1)    // 추후 로직을 통해 수정
                .rating(5.0)        // 추후 로직을 통해 수정
                .lastModifiedAt(course.getUpdatedAt())
                .build();
    }
}

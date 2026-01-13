package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseLevel;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CourseResult(
        Long courseId,
        String title,
        String summary,
        String instructorName,
        List<String> categories,
        String thumbnailResourceKey,
        CourseStatus status,
        int price,
        CourseLevel level,
        int studentCount,
        double rating,
        LocalDateTime lastModifiedAt
) {
    public static CourseResult of(Course course, List<String> categoryNames, String instructorName, int studentCount) {
        return CourseResult.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .summary(course.getSummary())
                .instructorName(instructorName)
                .categories(categoryNames)
                .thumbnailResourceKey(course.getThumbnailResourceKey())
                .status(course.getCourseStatus())
                .price(course.getPrice())
                .level(course.getCourseLevel())
                .studentCount(studentCount)
                .rating(5.0)
                .lastModifiedAt(course.getUpdatedAt())
                .build();
    }
}

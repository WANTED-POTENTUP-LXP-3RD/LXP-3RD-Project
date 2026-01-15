package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.application.dto.ReviewStat;
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
        String thumbnailUrl,
        CourseStatus status,
        int price,
        CourseLevel level,
        int studentCount,
        ReviewStat reviewStat,
        LocalDateTime lastModifiedAt
) {
    public static CourseResult of(Course course, List<String> categoryNames, String instructorName, int studentCount, ReviewStat reviewStat) {
        return CourseResult.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .summary(course.getSummary())
                .instructorName(instructorName)
                .categories(categoryNames)
                .thumbnailUrl(course.getThumbnailResourceKey())
                .status(course.getCourseStatus())
                .price(course.getPrice())
                .level(course.getCourseLevel())
                .studentCount(studentCount)
                .reviewStat(reviewStat)
                .lastModifiedAt(course.getUpdatedAt())
                .build();
    }
}

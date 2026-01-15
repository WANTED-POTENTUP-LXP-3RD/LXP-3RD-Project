package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.application.dto.ReviewStat;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseLevel;
import com.lxp.aplus.course.domain.CourseStatus;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record CourseDetailResult(
        Long courseId,
        List<String> categories,
        String title,
        String summary,
        String description,
        ReviewStat reviewStat,
        int price,
        CourseStatus status,
        CourseLevel level,
        String thumbnailResourceKey,
        InstructorResult instructor,
        boolean isPurchased,
        int studentCount,
        int totalDuration,
        List<SectionResult> sections
) {
    public static CourseDetailResult of(Course course, List<String> categoryNames, InstructorResult instructor, boolean isPurchased, int studentCount, int totalDuration, ReviewStat reviewStat) {
        return CourseDetailResult.builder()
                .courseId(course.getId())
                .categories(categoryNames)
                .title(course.getTitle())
                .summary(course.getSummary())
                .description(course.getDescription())
                .reviewStat(reviewStat)
                .price(course.getPrice())
                .status(course.getCourseStatus())
                .level(course.getCourseLevel())
                .thumbnailResourceKey(course.getThumbnailResourceKey())
                .instructor(instructor)
                .isPurchased(isPurchased)
                .studentCount(studentCount)
                .totalDuration(totalDuration)
                .sections(course.getSections().stream()
                        .map(SectionResult::from)
                        .collect(Collectors.toList()))
                .build();
    }
}

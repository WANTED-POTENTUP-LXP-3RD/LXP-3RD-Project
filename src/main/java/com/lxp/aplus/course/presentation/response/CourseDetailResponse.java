package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.domain.Course;
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
    public static CourseDetailResponse of(Course course, List<String> categoryNames, InstructorResponse instructor, boolean isPurchased, int studentCount, int totalDuration) {
        return CourseDetailResponse.builder()
                .courseId(course.getId())
                .categories(categoryNames)
                .title(course.getTitle())
                .summary(course.getSummary())
                .description(course.getDescription())
                .price(course.getPrice())
                .status(course.getCourseStatus())
                .level(course.getCourseLevel())
                .thumbnailUrl(course.getThumbnailUrl())
                .instructor(instructor)
                .isPurchased(isPurchased)
                .studentCount(studentCount)
                .totalDuration(totalDuration)
                .sections(course.getSections().stream()
                        .map(SectionResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}

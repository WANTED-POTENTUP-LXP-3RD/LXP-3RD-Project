package com.lxp.aplus.course.application.mapper;

import com.lxp.aplus.course.application.dto.ReviewStat;
import com.lxp.aplus.course.application.result.CourseDetailResult;
import com.lxp.aplus.course.application.result.CourseResult;
import com.lxp.aplus.course.application.result.InstructorResult;
import com.lxp.aplus.course.domain.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseResultMapper {

    private final SectionResultMapper sectionResultMapper;

    public CourseResult toResult(Course course, List<String> categoryNames, String instructorName, int studentCount, ReviewStat reviewStat) {
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
                .reviewStat(reviewStat)
                .lastModifiedAt(course.getUpdatedAt())
                .build();
    }

    public CourseDetailResult toDetailResult(Course course, List<String> categoryNames, InstructorResult instructor, boolean isPurchased, int studentCount, int totalDuration, ReviewStat reviewStat) {
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
                .sections(sectionResultMapper.toResults(course.getSections()))
                .build();
    }
}

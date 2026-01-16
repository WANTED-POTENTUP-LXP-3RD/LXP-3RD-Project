package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.UserErrorCode;
import com.lxp.aplus.course.application.mapper.CourseResultMapper;
import com.lxp.aplus.course.application.port.out.CategoryQueryPort;
import com.lxp.aplus.course.application.port.out.EnrollmentQueryPort;
import com.lxp.aplus.course.application.port.out.ReviewQueryPort;
import com.lxp.aplus.course.application.port.out.UserQueryPort;
import com.lxp.aplus.course.application.result.CourseDetailResult;
import com.lxp.aplus.course.application.result.CourseResult;
import com.lxp.aplus.course.application.result.InstructorResult;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewSummary;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseQueryUseCase {
    private final CourseResultMapper courseResultMapper;
    private final CourseRepository courseRepository;
    private final UserQueryPort userQueryPort;
    private final CategoryQueryPort categoryQueryPort;
    private final EnrollmentQueryPort enrollmentQueryPort;
    private final ReviewQueryPort reviewQueryPort;

    public Page<CourseResult> getInstructorCourses(Long instructorId, Pageable pageable) {
        Page<Course> courses = courseRepository.findAllByInstructorIdExcludingDeleted(instructorId, pageable);
        return convertToCourseResponse(courses, pageable);
    }

    public Page<CourseResult> getPublishedCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findAllPublished(pageable);
        return convertToCourseResponse(courses, pageable);
    }

    private Page<CourseResult> convertToCourseResponse(Page<Course> courses, Pageable pageable) {
        List<Long> categoryIds = courses.getContent().stream()
                .map(Course::getCategoryId)
                .distinct()
                .toList();

        List<Long> courseIds = courses.getContent().stream()
                .map(Course::getId)
                .toList();

        Map<Long, List<String>> categoryNamesMap = categoryQueryPort.getCategoryNamesBatch(categoryIds);
        Map<Long, Integer> studentCountMap = enrollmentQueryPort.getStudentCountBatch(courseIds);
        Map<Long, ReviewSummary> reviewInfoMap = reviewQueryPort.getReviewInfos(courseIds).stream()
                .collect(Collectors.toMap(ReviewSummary::courseId, info -> info));

        List<CourseResult> courseResponses = courses.getContent().stream()
                .map(course -> {
                    String instructorName = userQueryPort.findInstructorById(course.getInstructorId())
                            .map(InstructorResult::nickName)
                            .orElse("알 수 없음");

                    ReviewSummary reviewInfo = reviewInfoMap.getOrDefault(course.getId(), ReviewSummary.defaultValue());

                    return courseResultMapper.toResult(
                            course,
                            categoryNamesMap.get(course.getCategoryId()),
                            instructorName,
                            studentCountMap.getOrDefault(course.getId(), 0),
                            com.lxp.aplus.course.application.dto.ReviewStat.from(reviewInfo)
                    );
                })
                .toList();

        return new PageImpl<>(courseResponses, pageable, courses.getTotalElements());
    }

    private List<String> getCategoryNames(Long categoryId) {
        if (categoryId == null) {
            return Collections.emptyList();
        }
        return categoryQueryPort.findCategoryWithParentNames(categoryId);
    }

    public CourseDetailResult getPublishedCourseDetail(Long courseId, Long userId) {
        Course course = courseRepository.findPublishedWithCurriculumById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        Map<Long, ReviewSummary> reviewInfoMap = reviewQueryPort.getReviewInfos(List.of(courseId)).stream()
                .collect(Collectors.toMap(ReviewSummary::courseId, info -> info));
        com.lxp.aplus.course.application.dto.ReviewStat reviewStat = com.lxp.aplus.course.application.dto.ReviewStat.from(reviewInfoMap.getOrDefault(course.getId(), ReviewSummary.defaultValue()));

        List<String> categoryNames = getCategoryNames(course.getCategoryId());

        InstructorResult instructorResult = userQueryPort.findInstructorById(course.getInstructorId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        int totalDuration = calculateTotalDuration(course);

        boolean isPurchased = enrollmentQueryPort.isEnrolled(userId, courseId);
        int studentCount = enrollmentQueryPort.getStudentCount(courseId);
        return courseResultMapper.toDetailResult(course, categoryNames, instructorResult, isPurchased, studentCount, totalDuration,
                reviewStat);
    }

    public CourseDetailResult getInstructorCourseDetail(Long courseId, Long instructorId) {
        Course course = courseRepository.findWithCurriculumById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        Map<Long, ReviewSummary> reviewInfoMap = reviewQueryPort.getReviewInfos(List.of(courseId)).stream()
                .collect(Collectors.toMap(ReviewSummary::courseId, info -> info));
        com.lxp.aplus.course.application.dto.ReviewStat reviewStat = com.lxp.aplus.course.application.dto.ReviewStat.from(reviewInfoMap.getOrDefault(course.getId(), ReviewSummary.defaultValue()));

        course.validateOwner(instructorId);

        List<String> categoryNames = getCategoryNames(course.getCategoryId());

        InstructorResult instructorResult = userQueryPort.findInstructorById(course.getInstructorId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        int totalDuration = calculateTotalDuration(course);

        boolean isPurchased = enrollmentQueryPort.isEnrolled(instructorId, courseId);
        int studentCount = enrollmentQueryPort.getStudentCount(courseId);
        return courseResultMapper.toDetailResult(course, categoryNames, instructorResult, isPurchased, studentCount, totalDuration,
                reviewStat);
    }

    private int calculateTotalDuration(Course course) {
        return course.getSections().stream()
                .flatMap(section -> section.getLectures().stream())
                .mapToInt(lecture -> lecture.getTotalDurationSeconds() != null ? lecture.getTotalDurationSeconds() : 0)
                .sum();
    }
}

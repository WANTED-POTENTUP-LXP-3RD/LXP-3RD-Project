package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.UserErrorCode;
import com.lxp.aplus.course.application.port.out.CategoryQueryPort;
import com.lxp.aplus.course.application.port.out.UserQueryPort;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.presentation.response.CourseDetailResponse;
import com.lxp.aplus.course.presentation.response.CourseResponse;
import com.lxp.aplus.course.presentation.response.InstructorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseQueryUseCase {
    private final CourseRepository courseRepository;
    private final UserQueryPort userQueryPort;
    private final CategoryQueryPort categoryQueryPort;

    public Page<CourseResponse> getInstructorCourses(Long instructorId, Pageable pageable) {
        Page<Course> courses = courseRepository.findAllByInstructorId(instructorId, pageable);
        return convertToCourseResponse(courses, pageable);
    }

    public Page<CourseResponse> getPublishedCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findAllByPublished(pageable);
        return convertToCourseResponse(courses, pageable);
    }

    private Page<CourseResponse> convertToCourseResponse(Page<Course> courses, Pageable pageable) {
        List<CourseResponse> courseResponses = courses.getContent().stream()
                .map(course -> {
                    List<String> categoryPath = getCategoryNames(course.getCategoryId());
                    return CourseResponse.of(course, categoryPath);
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

    public CourseDetailResponse getInstructorCourseDetail(Long courseId, Long instructorId) {
        Course course = courseRepository.findWithCurriculumById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.validateOwner(instructorId);
        List<String> categoryNames = getCategoryNames(course.getCategoryId());

        InstructorResponse instructorResponse = userQueryPort.findInstructorById(course.getInstructorId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        int totalDuration = course.getSections().stream()
                .flatMap(section -> section.getLectures().stream())
                .mapToInt(lecture -> lecture.getTotalDurationSeconds() != null ? lecture.getTotalDurationSeconds() : 0)
                .sum();

        // TODO: enrollmentQueryPort를 통해 수강 완료 여부 조회 & 수강생 수 조회
        boolean isPurchased = false;
        int studentCount = 0;

        return CourseDetailResponse.of(course, categoryNames, instructorResponse, isPurchased, studentCount, totalDuration);
    }

    public CourseDetailResponse getPublishedCourseDetail(Long courseId, Long userId) {
        Course course = courseRepository.findPublishedWithCurriculumById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        List<String> categoryNames = getCategoryNames(course.getCategoryId());

        InstructorResponse instructorResponse = userQueryPort.findInstructorById(course.getInstructorId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        int totalDuration = course.getSections().stream()
                .flatMap(section -> section.getLectures().stream())
                .mapToInt(lecture -> lecture.getTotalDurationSeconds() != null ? lecture.getTotalDurationSeconds() : 0)
                .sum();

        // TODO: enrollmentQueryPort를 통해 수강 완료 여부 조회 & 수강생 수 조회
        boolean isPurchased = false;
        int studentCount = 0;

        return CourseDetailResponse.of(course, categoryNames, instructorResponse, isPurchased, studentCount, totalDuration);
    }
}

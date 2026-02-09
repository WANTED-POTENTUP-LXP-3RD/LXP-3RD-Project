package com.lxp.aplus.course.application.internal.usecase;

import com.lxp.aplus.course.application.internal.dto.CourseInternalResult;
import com.lxp.aplus.course.application.port.in.dto.ResourceSummary;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.user.application.internal.dto.UserInternalResult;
import com.lxp.aplus.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseInternalUseCase {
    private final CourseRepository courseRepository;

    public Optional<CourseInternalResult> findById(Long id) {
        return courseRepository.findById(id)
                .map(CourseInternalResult::from);
    }

    public Map<Long, CourseInternalResult> getCoursesByIds(List<Long> courseIds) {
        List<Course> courses = courseRepository.findByIdIn(courseIds);

        return courses.stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        CourseInternalResult::from
                ));
    }

    public List<ResourceSummary> findLectureSummariesByCourseId(Long courseId) {
        return courseRepository.findLectureSummariesByCourseId(courseId);
    }
}

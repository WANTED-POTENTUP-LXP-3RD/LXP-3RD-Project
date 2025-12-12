package com.lxp.aplus.course.infrastructure.adapter;

import com.lxp.aplus.course.application.port.out.EnrollmentQueryPort;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.infrastructure.persistence.dto.StudentCountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EnrollmentQueryAdapter implements EnrollmentQueryPort {
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public int getStudentCount(Long courseId) {
        return Math.toIntExact(enrollmentRepository.countByCourseId(courseId));
    }

    @Override
    public boolean isEnrolled(Long userId, Long courseId) {
        if (userId == null) {
            return false;
        }
        return enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId);
    }

    @Override
    public Map<Long, Integer> getStudentCountBatch(List<Long> courseIds) {
        return enrollmentRepository.findStudentCountsByCourseIds(courseIds)
                .stream()
                .collect(Collectors.toMap(
                        StudentCountDto::getCourseId,
                        dto -> Math.toIntExact(dto.getCnt()
                )));
    }

}

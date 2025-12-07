package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
import com.lxp.aplus.enrollment.application.result.EnrollmentCreationResult;
import com.lxp.aplus.enrollment.application.result.EnrollmentDetailResult;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentCommandUseCase{

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public EnrollmentCreationResult enroll(EnrollmentCommand command) {
        Long courseId = command.courseId();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_COURSE_NOT_FOUND));
        if (enrollmentRepository.existsByStudentIdAndCourseId(command.studentId(), courseId)) {
            throw new BusinessException(EnrollmentErrorCode.ALREADY_ENROLLED_COURSE);
        }

        LocalDateTime expiredAt = LocalDateTime.now().plusYears(2);
        
        Enrollment enrollmentToSave = Enrollment.of(command.studentId(), courseId, expiredAt);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollmentToSave);

        EnrollmentDetailResult createdEnrollment = EnrollmentDetailResult.from(savedEnrollment);

        return EnrollmentCreationResult.of(List.of(createdEnrollment));
    }
}

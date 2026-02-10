package com.lxp.aplus.payment.infrastructure.adapter;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.application.port.out.EnrollmentRepository;
import com.lxp.aplus.order.domain.OrderItem;
import com.lxp.aplus.payment.application.port.out.EnrollmentCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EnrollmentCommandPort의 구현체 (Adapter)
 * // TODO: 향후 이 메서드 내용은 Enrollment BC에서 구현하여 전달받도록 리팩터해야 합니다.
 */
@Component
@RequiredArgsConstructor
@Transactional
public class EnrollmentCommandAdapter implements EnrollmentCommandPort {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void enrollUserInCourses(Long userId, Map<Long, Long> courseToOrderItemMap) {

        // 1. 중복 수강 여부 확인 -> Enrollment 생성
        // TODO: Enrollment 도메인 규칙으로 이동
        List<Enrollment> newEnrollments = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : courseToOrderItemMap.entrySet()) {
            Long courseId = entry.getKey();
            Long orderItemId = entry.getValue();

            // TODO: N+1 문제 확인
            boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId);

            if (!isEnrolled) {
                Enrollment enrollment = Enrollment.create(
                        userId,
                        courseId,
                        orderItemId
                );

                newEnrollments.add(enrollment);
            }
        }

        // 3. Enrollment 저장
        // FIXME: 이 트랜잭션이 실패하면 Payment 트랜잭션도 롤백되는 문제 -> 추후 이벤트로 수정!! 🚨
        enrollmentRepository.saveAll(newEnrollments);
    }
}

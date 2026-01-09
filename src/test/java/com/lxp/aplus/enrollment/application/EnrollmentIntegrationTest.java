package com.lxp.aplus.enrollment.application;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseLevel;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.payment.application.command.PaymentConfirmCommand;
import com.lxp.aplus.payment.application.command.PaymentPrepareCommand;
import com.lxp.aplus.payment.application.usecase.PaymentCommandUseCase;
import com.lxp.aplus.payment.presentation.response.PaymentPrepareResponse;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("수강신청 통합 테스트")
public class EnrollmentIntegrationTest {

    @Autowired
    private PaymentCommandUseCase paymentCommandUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private User testStudent;
    private User testInstructor;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        String uniqueId = String.valueOf(System.nanoTime());
        // Given: 테스트용 강사, 학생, 강의 데이터 생성
        testInstructor = userRepository.save(User.of("강사님_" + uniqueId, "강사님닉네임_" + uniqueId, "instructor_" + uniqueId + "@example.com", "password123", "01000000000"));
        testStudent = userRepository.save(User.of("테스트학생_" + uniqueId, "테스트학생닉네임_" + uniqueId, "student_" + uniqueId + "@example.com", "password123", "01011112222"));
        testCourse = courseRepository.save(Course.builder()
                .instructorId(testInstructor.getId())
                .categoryId(1L) // 테스트용 카테고리 ID
                .title("테스트 강의_" + uniqueId)
                .summary("강의 요약")
                .description("강의 설명입니다.")
                .thumbnailUrl("http://example.com/thumbnail.png")
                .price(10000)
                .courseLevel(CourseLevel.BEGINNER)
                .build());
    }

    @Commit
    @Test
    @DisplayName("결제가 성공적으로 완료되면 수강신청이 생성되고, 현재 로직에 따라 orderItemId는 null이어야 한다")
    void should_create_enrollment_with_null_orderItemId_when_payment_is_confirmed() {
        // Given: 결제 준비 단계 실행
        PaymentPrepareCommand prepareCommand = new PaymentPrepareCommand(
                testStudent.getId(),
                Collections.singletonList(testCourse.getId())
        );
        PaymentPrepareResponse prepareResult = paymentCommandUseCase.prepare(prepareCommand);
        String orderId = prepareResult.orderId(); // record style getter
        assertThat(orderId).isNotNull();

        // When: 결제 확정 단계 실행
        String uniquePaymentKey = "test_imp_uid_" + System.nanoTime();
        PaymentConfirmCommand confirmCommand = new PaymentConfirmCommand(
                testStudent.getId(),
                orderId,
                uniquePaymentKey,
                BigDecimal.valueOf(10000L)
        );
        paymentCommandUseCase.confirm(confirmCommand);

        // Then: 수강신청 데이터가 DB에 생성되었는지 검증
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(testStudent.getId(), EnrollmentStatus.ENROLLED, Pageable.unpaged()).getContent();

        assertThat(enrollments).hasSize(1);
        Enrollment createdEnrollment = enrollments.get(0);

        assertThat(createdEnrollment.getStudentId()).isEqualTo(testStudent.getId());
        assertThat(createdEnrollment.getCourseId()).isEqualTo(testCourse.getId());
        assertThat(createdEnrollment.getOrderItemId()).isNull(); // 현재 구현상 orderItemId는 null이어야 함
    }
}
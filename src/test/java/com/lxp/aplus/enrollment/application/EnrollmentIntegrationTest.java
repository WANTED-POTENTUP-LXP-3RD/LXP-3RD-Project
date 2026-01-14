package com.lxp.aplus.enrollment.application;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseLevel;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
import com.lxp.aplus.enrollment.application.event.EnrollmentCanceledEvent;
import com.lxp.aplus.enrollment.application.usecase.EnrollmentCommandUseCase;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("수강 통합 테스트")
@RecordApplicationEvents
public class EnrollmentIntegrationTest {

    @Autowired
    private EnrollmentCommandUseCase enrollmentCommandUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private EntityManager entityManager;

    private User testStudent;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        String uniqueId = String.valueOf(System.nanoTime());
        // Given
        User testInstructor = userRepository.save(User.of("강사님_" + uniqueId, "강사님닉네임_" + uniqueId, "instructor_" + uniqueId + "@example.com", "password123", "01000000000"));
        testStudent = userRepository.save(User.of("테스트학생_" + uniqueId, "테스트학생닉네임_" + uniqueId, "student_" + uniqueId + "@example.com", "password123", "01011112222"));
        testCourse = courseRepository.save(Course.builder()
                .instructorId(testInstructor.getId())
                .categoryId(1L)
                .title("테스트 강의_" + uniqueId)
                .summary("강의 요약")
                .description("강의 설명입니다.")
                .thumbnailUrl("http://example.com/thumbnail.png")
                .price(10000)
                .courseLevel(CourseLevel.BEGINNER)
                .build());
    }

    @Nested
    @DisplayName("수강 신청")
    class EnrollTest {
        @Test
        @DisplayName("성공: 정상적인 요청 시 수강 정보가 DB에 저장된다.")
        void enroll_integrationTest_success() {
            // given
            long testOrderItemId = 2000L;
            EnrollmentCommand command = new EnrollmentCommand(testStudent.getId(), testCourse.getId(), testOrderItemId);

            // when
            enrollmentCommandUseCase.enroll(command);

            // then
            entityManager.flush();
            entityManager.clear();

            Enrollment createdEnrollment = enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()).orElseThrow();

            assertThat(createdEnrollment.getStudentId()).isEqualTo(testStudent.getId());
            assertThat(createdEnrollment.getCourseId()).isEqualTo(testCourse.getId());
            assertThat(createdEnrollment.getOrderItemId()).isEqualTo(testOrderItemId);
            assertThat(createdEnrollment.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
        }
    }


    @Nested
    @DisplayName("수강 취소")
    class CancelTest {
        private Enrollment testEnrollment;

        @BeforeEach
        void setUp() {
            testEnrollment = enrollmentRepository.save(Enrollment.create(testStudent.getId(), testCourse.getId(), 12345L));
            entityManager.flush();
            entityManager.clear();
        }

        @Test
        @DisplayName("성공: 학습 이력이 없을 때")
        void cancel_success_when_no_progress() {
            // when
            enrollmentCommandUseCase.cancel(testEnrollment.getId(), testStudent.getId());
            entityManager.flush();
            entityManager.clear();

            // then
            Enrollment canceledEnrollment = enrollmentRepository.findById(testEnrollment.getId()).orElseThrow();
            assertThat(canceledEnrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);

            assertThat(progressRepository.existsByEnrollmentId(testEnrollment.getId())).isFalse();

            long eventCount = applicationEvents.stream(EnrollmentCanceledEvent.class).count();
            assertThat(eventCount).isEqualTo(1);
        }

        @Test
        @DisplayName("실패: 이미 학습을 시작했을 때")
        void cancel_fail_when_progress_exists() {
            // given
            progressRepository.save(Progress.of(testEnrollment.getId(), 1L));
            entityManager.flush();
            entityManager.clear();


            // when & then
            assertThatThrownBy(() -> enrollmentCommandUseCase.cancel(testEnrollment.getId(), testStudent.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(EnrollmentErrorCode.CANNOT_CANCEL_AFTER_STARTED.getMessage());
        }

        @Test
        @DisplayName("실패: 소유자가 아닐 때")
        void cancel_fail_when_not_owner() {
            // given
            User anotherStudent = userRepository.save(User.of("다른학생", "다른학생닉네임", "another@example.com", "password", "01033334444"));

            // when & then
            assertThatThrownBy(() -> enrollmentCommandUseCase.cancel(testEnrollment.getId(), anotherStudent.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS.getMessage());
        }

        @Test
        @DisplayName("실패: 이미 취소된 수강일 때")
        void cancel_fail_when_already_canceled() {
            // given
            enrollmentCommandUseCase.cancel(testEnrollment.getId(), testStudent.getId());
            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThatThrownBy(() -> enrollmentCommandUseCase.cancel(testEnrollment.getId(), testStudent.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(EnrollmentErrorCode.ALREADY_CANCELLED_ENROLLMENT.getMessage());
        }
    }
}
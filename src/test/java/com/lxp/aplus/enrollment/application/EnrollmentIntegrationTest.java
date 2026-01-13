package com.lxp.aplus.enrollment.application;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseLevel;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
import com.lxp.aplus.enrollment.application.usecase.EnrollmentCommandUseCase;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("수강신청 통합 테스트")
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

    @Test
    @DisplayName("수강 신청 통합 테스트: 정상적인 요청 시 수강 정보가 DB에 저장된다.")
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
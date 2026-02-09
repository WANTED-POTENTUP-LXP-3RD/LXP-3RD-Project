//package com.lxp.aplus.enrollment.application;
//
//import com.lxp.aplus.common.error.BusinessException;
//import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
//import com.lxp.aplus.course.domain.*;
//import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
//import com.lxp.aplus.enrollment.application.event.EnrollmentCanceledEvent;
//import com.lxp.aplus.enrollment.application.usecase.EnrollmentCommandUseCase;
//import com.lxp.aplus.enrollment.domain.Enrollment;
//import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
//import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
//import com.lxp.aplus.progress.domain.Progress;
//import com.lxp.aplus.progress.domain.ProgressRepository;
//import com.lxp.aplus.user.domain.User;
//import com.lxp.aplus.user.domain.UserRepository;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.event.ApplicationEvents;
//import org.springframework.test.context.event.RecordApplicationEvents;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@SpringBootTest
//@Transactional
//@DisplayName("수강 통합 테스트")
//@RecordApplicationEvents
//public class EnrollmentIntegrationTest {
//
//    @Autowired
//    private EnrollmentCommandUseCase enrollmentCommandUseCase;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private CourseRepository courseRepository;
//    @Autowired
//    private EnrollmentRepository enrollmentRepository;
//    @Autowired
//    private ProgressRepository progressRepository;
//    @Autowired
//    private ApplicationEvents applicationEvents;
//    @Autowired
//    private EntityManager entityManager;
//
//    private User testStudent;
//    private Course testCourse;
//
//    @BeforeEach
//    void setUp() {
//        User testInstructor = givenUser("강사님");
//        testStudent = givenUser("테스트학생");
//        testCourse = givenCourse(testInstructor, "테스트 강의");
//    }
//
//    @Nested
//    @DisplayName("수강 신청")
//    class EnrollTest {
//        @Test
//        @DisplayName("성공: 정상적인 요청 시 수강 정보가 DB에 저장된다.")
//        void enroll_integrationTest_success() {
//            // given
//            long testOrderItemId = 2000L;
//            EnrollmentCommand command = new EnrollmentCommand(testStudent.getId(), testCourse.getId(), testOrderItemId);
//
//            // when
//            enrollmentCommandUseCase.enroll(command);
//            flushAndClearPersistenceContext();
//
//            // then
//            Enrollment createdEnrollment = enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()).orElseThrow();
//            assertThat(createdEnrollment.getStudentId()).isEqualTo(testStudent.getId());
//            assertThat(createdEnrollment.getCourseId()).isEqualTo(testCourse.getId());
//            assertThat(createdEnrollment.getOrderItemId()).isEqualTo(testOrderItemId);
//            assertThat(createdEnrollment.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
//        }
//    }
//
//    @Nested
//    @DisplayName("수강 취소")
//    class CancelTest {
//        private Enrollment testEnrollment;
//
//        @BeforeEach
//        void setUp() {
//            testEnrollment = givenEnrollment(testStudent, testCourse, 12345L);
//        }
//
//        @Test
//        @DisplayName("성공: 학습 이력이 없을 때")
//        void cancel_success_when_no_progress() {
//            // when
//            enrollmentCommandUseCase.cancel(testEnrollment.getId(), testStudent.getId());
//            flushAndClearPersistenceContext();
//
//            // then
//            Enrollment canceledEnrollment = enrollmentRepository.findById(testEnrollment.getId()).orElseThrow();
//            assertThat(canceledEnrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
//            assertThat(progressRepository.existsByEnrollmentId(testEnrollment.getId())).isFalse();
//
//            long eventCount = applicationEvents.stream(EnrollmentCanceledEvent.class).count();
//            assertThat(eventCount).isEqualTo(1);
//        }
//
//        @Test
//        @DisplayName("실패: 이미 학습을 시작했을 때")
//        void cancel_fail_when_progress_exists() {
//            // given
//            givenProgressExistsForEnrollment(testCourse, testEnrollment);
//
//            // when & then
//            assertThatThrownBy(() -> enrollmentCommandUseCase.cancel(testEnrollment.getId(), testStudent.getId()))
//                    .isInstanceOf(BusinessException.class)
//                    .hasMessage(EnrollmentErrorCode.ENROLLMENT_CANNOT_CANCEL_AFTER_STARTED.getMessage());
//        }
//
//        @Test
//        @DisplayName("실패: 소유자가 아닐 때")
//        void cancel_fail_when_not_owner() {
//            // given
//            User anotherStudent = givenUser("다른학생");
//
//            // when & then
//            assertThatThrownBy(() -> enrollmentCommandUseCase.cancel(testEnrollment.getId(), anotherStudent.getId()))
//                    .isInstanceOf(BusinessException.class)
//                    .hasMessage(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS.getMessage());
//        }
//
//        @Test
//        @DisplayName("실패: 이미 취소된 수강일 때")
//        void cancel_fail_when_already_canceled() {
//            // given
//            enrollmentCommandUseCase.cancel(testEnrollment.getId(), testStudent.getId());
//            flushAndClearPersistenceContext();
//
//            // when & then
//            assertThatThrownBy(() -> enrollmentCommandUseCase.cancel(testEnrollment.getId(), testStudent.getId()))
//                    .isInstanceOf(BusinessException.class)
//                    .hasMessage(EnrollmentErrorCode.ENROLLMENT_ALREADY_CANCELLED.getMessage());
//        }
//    }
//
//    // ============================= GIVEN HELPER METHODS ============================= //
//
//    private User givenUser(String name) {
//        String uniqueId = name + "_" + System.nanoTime();
//        return userRepository.save(User.of(uniqueId, uniqueId + "_nickname", uniqueId + "@example.com", "password123", "01012345678"));
//    }
//
//    private Course givenCourse(User instructor, String title) {
//        String uniqueId = title + "_" + System.nanoTime();
//        return courseRepository.save(Course.builder()
//                .instructorId(instructor.getId())
//                .categoryId(1L)
//                .title(uniqueId)
//                .summary("강의 요약")
//                .description("강의 설명입니다.")
//                .thumbnailUrl("http://example.com/thumbnail.png")
//                .price(10000)
//                .courseLevel(CourseLevel.BEGINNER)
//                .build());
//    }
//
//    private Enrollment givenEnrollment(User student, Course course, Long orderItemId) {
//        Enrollment enrollment = enrollmentRepository.save(Enrollment.create(student.getId(), course.getId(), orderItemId));
//        flushAndClearPersistenceContext();
//        return enrollment;
//    }
//
//    private void givenProgressExistsForEnrollment(Course course, Enrollment enrollment) {
//        // 1. Course 애그리게잇의 비즈니스 메서드를 사용해 Section 추가
//        course.addSection("테스트 섹션", 1);
//
//        // 2. CourseRepository를 통해 Course와 Section을 저장
//        Course managedCourse = courseRepository.save(course);
//        courseRepository.flush();
//        Section managedSection = managedCourse.getSections().get(0);
//
//        // 3. Course 애그리게잇을 통해 Lecture 추가
//        managedCourse.createLecture(
//                managedSection.getId(), "테스트 강의", 120, false, 1,
//                false, "key", "url", "test.mp4"
//        );
//
//        // 4. ★★★ 중요: 변경된 Course 애그리게잇을 다시 저장하여 Lecture와 LectureResource를 영속화
//        courseRepository.save(managedCourse);
//        courseRepository.flush();
//
//        // 5. 이제 DB에 저장되었으므로, ID가 부여된 LectureResource를 가져올 수 있음
//        LectureResource resource = managedCourse.getSections().get(0).getLectures().get(0).getLectureResources().get(0);
//
//        // 6. 학습 이력(Progress) 생성
//        progressRepository.save(Progress.of(enrollment.getId(), resource.getId()));
//        flushAndClearPersistenceContext();
//    }
//
//    private void flushAndClearPersistenceContext() {
//        entityManager.flush();
//        entityManager.clear();
//    }
//}

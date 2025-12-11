package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.LectureResourceSummary;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.progress.application.result.LearningHistoryResult;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import com.lxp.aplus.user.domain.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import com.lxp.aplus.course.domain.LectureResource;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ProgressQueryUseCaseTest {

    @InjectMocks
    private ProgressQueryUseCase progressQueryUseCase;

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseFinder courseFinder;
    @Mock
    private ProgressRepository progressRepository;

    private UserInfo currentUser;
    private Long STUDENT_ID = 10L;
    private Long ENROLLMENT_ID = 1L;
    private Long COURSE_ID = 100L;
    private Long RESOURCE_ID_1 = 3001L;
    private Long RESOURCE_ID_2 = 3002L;
    private Long RESOURCE_ID_3 = 3003L;

    @BeforeEach
    void setUp() {
        currentUser = new UserInfo(STUDENT_ID, List.of(RoleType.STUDENT));
    }

    @Test
    @DisplayName("성공 - 학습 이력을 조회해야 한다.")
    void getLearningHistory_success() {
        // given
        Enrollment enrollment = Enrollment.builder()
                .id(ENROLLMENT_ID)
                .studentId(STUDENT_ID)
                .courseId(COURSE_ID)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .status(EnrollmentStatus.ENROLLED)
                .build();
        ReflectionTestUtils.setField(enrollment, "createdAt", LocalDateTime.now().minusDays(10));

        List<LectureResourceSummary> lectureResourceSummaries = List.of(
                new LectureResourceSummary(RESOURCE_ID_1, "Spring Boot 개념 설명", 240),
                new LectureResourceSummary(RESOURCE_ID_2, "Spring Data JPA 활용", 300),
                new LectureResourceSummary(RESOURCE_ID_3, "RESTful API 설계", 180)
        );
        
        LectureResource lr1 = mock(LectureResource.class);
        given(lr1.getId()).willReturn(RESOURCE_ID_1);

        LectureResource lr2 = mock(LectureResource.class);
        given(lr2.getId()).willReturn(RESOURCE_ID_2);
        
        Progress progress1 = Progress.builder()
                .enrollment(enrollment)
                .lectureResource(lr1)
                .watchedDuration(180)
                .isCompleted(true)
                .lastWatchedAt(LocalDateTime.now().minusHours(1))
                .build();
        
        Progress progress2 = Progress.builder()
                .enrollment(enrollment)
                .lectureResource(lr2)
                .watchedDuration(90)
                .isCompleted(false)
                .lastWatchedAt(LocalDateTime.now().minusHours(2))
                .build();

        List<Progress> progresses = List.of(progress1, progress2);

        // Mocking
        given(enrollmentRepository.findById(ENROLLMENT_ID)).willReturn(Optional.of(enrollment));
        given(courseFinder.findAllLectureResourcesByCourseId(COURSE_ID)).willReturn(lectureResourceSummaries);
        given(progressRepository.findByEnrollmentIdAndLectureResourceIds(any(Long.class), any(List.class)))
                .willReturn(progresses);


        // when
        LearningHistoryResult result = progressQueryUseCase.getLearningHistory(currentUser.id(), ENROLLMENT_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.enrollmentId()).isEqualTo(ENROLLMENT_ID);
        assertThat(result.overallProgressRate()).isEqualTo(33);
        
        assertThat(result.lastWatchedVideoId()).isEqualTo(RESOURCE_ID_1);
        assertThat(result.lastWatchedDurationOfLastVideo()).isEqualTo(180);
        assertThat(result.lastWatchedAt()).isNotNull();

        assertThat(result.lectureProgresses()).hasSize(3);
        assertThat(result.lectureProgresses().get(0).resourceId()).isEqualTo(RESOURCE_ID_1);
        assertThat(result.lectureProgresses().get(0).isCompleted()).isTrue();
        assertThat(result.lectureProgresses().get(0).watchedDuration()).isEqualTo(180);
        assertThat(result.lectureProgresses().get(0).currentProgressRate()).isEqualTo(75);

        assertThat(result.lectureProgresses().get(1).resourceId()).isEqualTo(RESOURCE_ID_2);
        assertThat(result.lectureProgresses().get(1).isCompleted()).isFalse();
        assertThat(result.lectureProgresses().get(1).watchedDuration()).isEqualTo(90);
        assertThat(result.lectureProgresses().get(1).currentProgressRate()).isEqualTo(30);

        assertThat(result.lectureProgresses().get(2).resourceId()).isEqualTo(RESOURCE_ID_3);
        assertThat(result.lectureProgresses().get(2).isCompleted()).isFalse();
        assertThat(result.lectureProgresses().get(2).watchedDuration()).isEqualTo(0);
        assertThat(result.lectureProgresses().get(2).currentProgressRate()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("실패 - 수강 내역을 찾을 수 없을 때 예외를 발생시켜야 한다.")
    void getLearningHistory_fail_enrollmentNotFound() {
        // given
        given(enrollmentRepository.findById(ENROLLMENT_ID)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressQueryUseCase.getLearningHistory(currentUser.id(), ENROLLMENT_ID));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }

    @Test
    @DisplayName("실패 - 다른 학생의 수강 내역 조회 시 예외를 발생시켜야 한다.")
    void getLearningHistory_fail_accessDenied() {
        // given
        Enrollment othersEnrollment = Enrollment.builder().id(ENROLLMENT_ID).studentId(STUDENT_ID + 1).build();
        given(enrollmentRepository.findById(ENROLLMENT_ID)).willReturn(Optional.of(othersEnrollment));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressQueryUseCase.getLearningHistory(currentUser.id(), ENROLLMENT_ID));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }

    @Test
    @DisplayName("실패 - 수강 기간이 만료되었을 때 예외를 발생시켜야 한다.")
    void getLearningHistory_fail_enrollmentExpired() {
        // given
        Enrollment expiredEnrollment = Enrollment.builder()
                .id(ENROLLMENT_ID)
                .studentId(STUDENT_ID)
                .courseId(COURSE_ID)
                .expiredAt(LocalDateTime.now().minusDays(1))
                .build();
        given(enrollmentRepository.findById(ENROLLMENT_ID)).willReturn(Optional.of(expiredEnrollment));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressQueryUseCase.getLearningHistory(currentUser.id(), ENROLLMENT_ID));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_EXPIRED_HISTORY_ACCESS_DENIED);
    }

    @Test
    @DisplayName("실패 - 학습할 리소스가 없을 때 예외를 발생시켜야 한다.")
    void getLearningHistory_fail_noResources() {
        // given
        Enrollment enrollment = Enrollment.builder()
                .id(ENROLLMENT_ID)
                .studentId(STUDENT_ID)
                .courseId(COURSE_ID)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        given(enrollmentRepository.findById(ENROLLMENT_ID)).willReturn(Optional.of(enrollment));
        given(courseFinder.findAllLectureResourcesByCourseId(COURSE_ID)).willReturn(Collections.emptyList());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressQueryUseCase.getLearningHistory(currentUser.id(), ENROLLMENT_ID));
        assertThat(exception.getErrorCode()).isEqualTo(ProgressErrorCode.LEARNING_HISTORY_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - 학습 이력이 없을 때 예외를 발생시켜야 한다. (아직 학습을 시작하지 않음)")
    void getLearningHistory_fail_noProgressYet() {
        // given
        Enrollment enrollment = Enrollment.builder()
                .id(ENROLLMENT_ID)
                .studentId(STUDENT_ID)
                .courseId(COURSE_ID)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        List<LectureResourceSummary> lectureResourceSummaries = List.of(
                new LectureResourceSummary(RESOURCE_ID_1, "Spring Boot 개념 설명", 240)
        );
        
        given(enrollmentRepository.findById(ENROLLMENT_ID)).willReturn(Optional.of(enrollment));
        given(courseFinder.findAllLectureResourcesByCourseId(COURSE_ID)).willReturn(lectureResourceSummaries);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressQueryUseCase.getLearningHistory(currentUser.id(), ENROLLMENT_ID));
        assertThat(exception.getErrorCode()).isEqualTo(ProgressErrorCode.LEARNING_HISTORY_NOT_FOUND);
    }
}
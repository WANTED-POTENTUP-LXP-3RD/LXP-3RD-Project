package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.progress.application.port.LectureDetailInfo;
import com.lxp.aplus.progress.application.port.LectureProvider;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import com.lxp.aplus.progress.presentation.response.CourseProgressResponse;
import com.lxp.aplus.progress.presentation.response.LectureProgressResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProgressQueryUseCaseTest {

    @InjectMocks
    private ProgressQueryUseCase progressQueryUseCase;

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private ProgressRepository progressRepository;
    @Mock
    private LectureProvider lectureProvider;

    @Test
    @DisplayName("성공 - 학습 이력을 정확히 조회한다")
    void getCourseProgress_success() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        Long resourceId1 = 301L;
        Long resourceId2 = 302L;

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(userId).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
        List<LectureDetailInfo> lectureDetails = List.of(
                new LectureDetailInfo(resourceId1, "Lecture 1", 100),
                new LectureDetailInfo(resourceId2, "Lecture 2", 200)
        );

        Progress progress1 = Progress.of(enrollmentId, resourceId1);
        progress1.updateProgress(100, 100); // Completed

        List<Progress> progresses = List.of(progress1);

        given(enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)).willReturn(Optional.of(enrollment));
        given(lectureProvider.getLectureDetailsByCourseId(courseId)).willReturn(lectureDetails);
        given(progressRepository.findByEnrollmentId(enrollmentId)).willReturn(progresses);

        // when
        CourseProgressResponse response = progressQueryUseCase.getCourseProgress(userId, courseId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEnrollmentId()).isEqualTo(enrollmentId);
        assertThat(response.getOverallProgressRate()).isEqualTo(50); // 1 of 2 lectures completed
        assertThat(response.getLastWatchedResourceId()).isEqualTo(resourceId1);
        assertThat(response.getLastWatchedAt()).isNotNull();

        List<LectureProgressResponse> lectureProgresses = response.getLectureProgresses();
        assertThat(lectureProgresses).hasSize(2);

        LectureProgressResponse lp1 = lectureProgresses.get(0);
        assertThat(lp1.getResourceId()).isEqualTo(resourceId1);
        assertThat(lp1.getTitle()).isEqualTo("Lecture 1");
        assertThat(lp1.isCompleted()).isTrue();
        assertThat(lp1.getProgressRate()).isEqualTo(100);

        LectureProgressResponse lp2 = lectureProgresses.get(1);
        assertThat(lp2.getResourceId()).isEqualTo(resourceId2);
        assertThat(lp2.getTitle()).isEqualTo("Lecture 2");
        assertThat(lp2.isCompleted()).isFalse();
        assertThat(lp2.getProgressRate()).isEqualTo(0);
    }

    @Test
    @DisplayName("성공 - 학습 이력이 없을 경우 0%로 조회된다")
    void getCourseProgress_success_noProgressYet() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        Long resourceId1 = 301L;

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(userId).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
        List<LectureDetailInfo> lectureDetails = List.of(new LectureDetailInfo(resourceId1, "Lecture 1", 100));

        given(enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)).willReturn(Optional.of(enrollment));
        given(lectureProvider.getLectureDetailsByCourseId(courseId)).willReturn(lectureDetails);
        given(progressRepository.findByEnrollmentId(enrollmentId)).willReturn(Collections.emptyList());

        // when
        CourseProgressResponse response = progressQueryUseCase.getCourseProgress(userId, courseId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOverallProgressRate()).isEqualTo(0);
        assertThat(response.getLastWatchedResourceId()).isNull();
        assertThat(response.getLectureProgresses()).hasSize(1);
        assertThat(response.getLectureProgresses().get(0).getProgressRate()).isEqualTo(0);
        assertThat(response.getLectureProgresses().get(0).isCompleted()).isFalse();
    }

    @Test
    @DisplayName("실패 - 수강 내역을 찾을 수 없으면 예외를 발생시킨다")
    void getCourseProgress_fail_whenEnrollmentNotFound() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        given(enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressQueryUseCase.getCourseProgress(userId, courseId));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }

    @Test
    @DisplayName("실패 - 수강 기간이 만료되었으면 예외를 발생시킨다")
    void getCourseProgress_fail_whenEnrollmentIsExpired() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        Enrollment expiredEnrollment = Enrollment.builder().id(enrollmentId).studentId(userId).courseId(courseId).expiredAt(LocalDateTime.now().minusDays(1)).build();
        given(enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)).willReturn(Optional.of(expiredEnrollment));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressQueryUseCase.getCourseProgress(userId, courseId));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_EXPIRED_HISTORY_ACCESS_DENIED);
    }
}

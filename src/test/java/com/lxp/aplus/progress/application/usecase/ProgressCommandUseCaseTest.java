package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.progress.application.command.ProgressUpdateCommand;
import com.lxp.aplus.progress.application.port.LectureInfo;
import com.lxp.aplus.progress.application.port.LectureProvider;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import com.lxp.aplus.progress.presentation.response.ProgressUpdateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProgressCommandUseCaseTest {

    @InjectMocks
    private ProgressCommandUseCase progressCommandUseCase;

    @Mock
    private ProgressRepository progressRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private LectureProvider lectureProvider;

    @Test
    @DisplayName("성공 - 새로운 학습 이력을 생성하고 진도율을 갱신한다")
    void updateProgress_success_createsNewProgress() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        Long resourceId = 300L;
        int watchedDuration = 60;
        int totalDuration = 300;

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(userId).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
        ProgressUpdateCommand command = new ProgressUpdateCommand(resourceId, watchedDuration);
        LectureInfo lectureInfo = new LectureInfo(totalDuration);

        given(enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)).willReturn(Optional.of(enrollment));
        given(lectureProvider.getLectureInfo(resourceId)).willReturn(lectureInfo);
        given(progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, resourceId)).willReturn(Optional.empty());
        given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> {
            Progress progressToSave = invocation.getArgument(0);
            return progressToSave;
        });

        // when
        ProgressUpdateResponse response = progressCommandUseCase.updateProgress(userId, courseId, command);

        // then
        ArgumentCaptor<Progress> progressCaptor = ArgumentCaptor.forClass(Progress.class);
        verify(progressRepository).save(progressCaptor.capture());
        Progress savedProgress = progressCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(savedProgress.getEnrollmentId()).isEqualTo(enrollmentId);
        assertThat(savedProgress.getLectureResourceId()).isEqualTo(resourceId);
        assertThat(savedProgress.getWatchedDuration()).isEqualTo(watchedDuration);
        assertThat(savedProgress.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("성공 - 기존 학습 이력을 찾아 완료 상태로 변경한다")
    void updateProgress_success_updatesExistingProgressToComplete() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        Long resourceId = 300L;
        int watchedDuration = 300;
        int totalDuration = 300;

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(userId).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
        Progress existingProgress = Progress.of(enrollmentId, resourceId);
        ProgressUpdateCommand command = new ProgressUpdateCommand(resourceId, watchedDuration);
        LectureInfo lectureInfo = new LectureInfo(totalDuration);

        given(enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)).willReturn(Optional.of(enrollment));
        given(lectureProvider.getLectureInfo(resourceId)).willReturn(lectureInfo);
        given(progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, resourceId)).willReturn(Optional.of(existingProgress));
        given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        progressCommandUseCase.updateProgress(userId, courseId, command);

        // then
        ArgumentCaptor<Progress> progressCaptor = ArgumentCaptor.forClass(Progress.class);
        verify(progressRepository).save(progressCaptor.capture());
        Progress savedProgress = progressCaptor.getValue();

        assertThat(savedProgress.isCompleted()).isTrue();
        assertThat(savedProgress.getWatchedDuration()).isEqualTo(watchedDuration);
    }

    @Test
    @DisplayName("실패 - 수강 내역을 찾을 수 없으면 예외를 발생시킨다")
    void updateProgress_fail_whenEnrollmentNotFound() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        ProgressUpdateCommand command = new ProgressUpdateCommand(300L, 150);

        given(enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressCommandUseCase.updateProgress(userId, courseId, command));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }

    @Test
    @DisplayName("실패 - 수강 기간이 만료되었으면 예외를 발생시킨다")
    void updateProgress_fail_whenEnrollmentIsExpired() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        ProgressUpdateCommand command = new ProgressUpdateCommand(300L, 150);
        Enrollment expiredEnrollment = Enrollment.builder().id(enrollmentId).studentId(userId).courseId(courseId).expiredAt(LocalDateTime.now().minusDays(1)).build();

        given(enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)).willReturn(Optional.of(expiredEnrollment));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressCommandUseCase.updateProgress(userId, courseId, command));
        assertThat(exception.getErrorCode()).isEqualTo(ProgressErrorCode.CANNOT_UPDATE_EXPIRED_ENROLLMENT);
    }

    @Test
    @DisplayName("실패 - 시청 시간이 영상의 전체 길이를 초과하면 예외를 발생시킨다")
    void updateProgress_fail_whenWatchedDurationExceedsTotal() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        Long resourceId = 300L;
        int watchedDuration = 301;
        int totalDuration = 300;

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(userId).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
        Progress existingProgress = Progress.of(enrollmentId, resourceId);
        ProgressUpdateCommand command = new ProgressUpdateCommand(resourceId, watchedDuration);
        LectureInfo lectureInfo = new LectureInfo(totalDuration);

        given(enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)).willReturn(Optional.of(enrollment));
        given(lectureProvider.getLectureInfo(resourceId)).willReturn(lectureInfo);
        given(progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, resourceId)).willReturn(Optional.of(existingProgress));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressCommandUseCase.updateProgress(userId, courseId, command));
        assertThat(exception.getErrorCode()).isEqualTo(ProgressErrorCode.WATCHED_DURATION_EXCEEDS_TOTAL);
    }
}
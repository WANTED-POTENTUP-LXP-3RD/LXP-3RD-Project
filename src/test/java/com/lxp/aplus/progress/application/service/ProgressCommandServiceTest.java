package com.lxp.aplus.progress.application.service;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.course.domain.ResourceType;
import com.lxp.aplus.progress.application.command.ProgressUpdateCommand;
import com.lxp.aplus.progress.application.dto.response.ProgressUpdateResponse;
import com.lxp.aplus.progress.application.port.out.EnrollmentCompletionPort;
import com.lxp.aplus.progress.application.port.out.EnrollmentReader;
import com.lxp.aplus.progress.application.port.out.LectureQueryPort;
import com.lxp.aplus.progress.application.port.out.dto.EnrollmentStatusDto;
import com.lxp.aplus.progress.application.port.out.dto.LectureDurationDto;
import com.lxp.aplus.progress.application.port.out.dto.LectureSummaryDto;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.application.port.out.ProgressRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProgressCommandServiceTest {

    @InjectMocks
    private ProgressCommandService progressCommandService;

    @Mock
    private ProgressRepository progressRepository;
    @Mock
    private EnrollmentReader enrollmentReader;
    @Mock
    private LectureQueryPort lectureProvider;
    @Mock
    private EnrollmentCompletionPort enrollmentCompletionPort;

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

        EnrollmentStatusDto enrollmentStatusDto = new EnrollmentStatusDto(enrollmentId, false, false);
        ProgressUpdateCommand command = new ProgressUpdateCommand(resourceId, watchedDuration);
        LectureDurationDto lectureDurationDto = new LectureDurationDto(totalDuration);

        given(enrollmentReader.findEnrollment(userId, courseId)).willReturn(Optional.of(enrollmentStatusDto));
        given(lectureProvider.getLectureInfo(resourceId)).willReturn(lectureDurationDto);
        given(lectureProvider.getLectureDetailsByCourseId(courseId)).willReturn(List.of());
        given(progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, resourceId)).willReturn(Optional.empty());
        given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ProgressUpdateResponse response = progressCommandService.updateProgress(userId, courseId, command);

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

        EnrollmentStatusDto enrollmentStatusDto = new EnrollmentStatusDto(enrollmentId, false, false);
        Progress existingProgress = Progress.of(enrollmentId, resourceId);
        ProgressUpdateCommand command = new ProgressUpdateCommand(resourceId, watchedDuration);
        LectureDurationDto lectureDurationDto = new LectureDurationDto(totalDuration);

        given(enrollmentReader.findEnrollment(userId, courseId)).willReturn(Optional.of(enrollmentStatusDto));
        given(lectureProvider.getLectureInfo(resourceId)).willReturn(lectureDurationDto);
        given(lectureProvider.getLectureDetailsByCourseId(courseId)).willReturn(List.of());
        given(progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, resourceId)).willReturn(Optional.of(existingProgress));
        given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        progressCommandService.updateProgress(userId, courseId, command);

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

        given(enrollmentReader.findEnrollment(userId, courseId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressCommandService.updateProgress(userId, courseId, command));
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
        EnrollmentStatusDto expiredEnrollmentStatusDto = new EnrollmentStatusDto(enrollmentId, false, true);

        given(enrollmentReader.findEnrollment(userId, courseId)).willReturn(Optional.of(expiredEnrollmentStatusDto));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressCommandService.updateProgress(userId, courseId, command));
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

        EnrollmentStatusDto enrollmentStatusDto = new EnrollmentStatusDto(enrollmentId, false, false);
        Progress existingProgress = Progress.of(enrollmentId, resourceId);
        ProgressUpdateCommand command = new ProgressUpdateCommand(resourceId, watchedDuration);
        LectureDurationDto lectureDurationDto = new LectureDurationDto(totalDuration);

        given(enrollmentReader.findEnrollment(userId, courseId)).willReturn(Optional.of(enrollmentStatusDto));
        given(lectureProvider.getLectureInfo(resourceId)).willReturn(lectureDurationDto);
        given(progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, resourceId)).willReturn(Optional.of(existingProgress));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressCommandService.updateProgress(userId, courseId, command));
        assertThat(exception.getErrorCode()).isEqualTo(ProgressErrorCode.WATCHED_DURATION_EXCEEDS_TOTAL);
    }

    @Test
    @DisplayName("성공 - 모든 리소스 완료 시 수강 상태를 COMPLETED로 전환한다")
    void updateProgress_success_completesEnrollmentWhenAllResourcesCompleted() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        Long resourceId1 = 300L;
        Long resourceId2 = 301L;
        int watchedDuration = 300;
        int totalDuration = 300;

        EnrollmentStatusDto enrollmentStatusDto = new EnrollmentStatusDto(enrollmentId, false, false);
        ProgressUpdateCommand command = new ProgressUpdateCommand(resourceId2, watchedDuration);
        LectureDurationDto lectureDurationDto = new LectureDurationDto(totalDuration);

        Progress progress1 = Progress.of(enrollmentId, resourceId1);
        progress1.updateProgress(totalDuration, totalDuration);
        Progress progress2 = Progress.of(enrollmentId, resourceId2);

        List<LectureSummaryDto> lectureDetails = List.of(
                new LectureSummaryDto(resourceId1, "Lecture1", totalDuration, ResourceType.VIDEO),
                new LectureSummaryDto(resourceId2, "Lecture2", totalDuration, ResourceType.VIDEO)
        );

        given(enrollmentReader.findEnrollment(userId, courseId)).willReturn(Optional.of(enrollmentStatusDto));
        given(lectureProvider.getLectureInfo(resourceId2)).willReturn(lectureDurationDto);
        given(lectureProvider.getLectureDetailsByCourseId(courseId)).willReturn(lectureDetails);
        given(progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, resourceId2)).willReturn(Optional.of(progress2));
        given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(progressRepository.findByEnrollmentId(enrollmentId)).willReturn(List.of(progress1, progress2));

        // when
        progressCommandService.updateProgress(userId, courseId, command);

        // then
        verify(enrollmentCompletionPort).completeEnrollment(enrollmentId);
    }

    @Test
    @DisplayName("성공 - 모든 리소스가 완료되지 않으면 수강 상태를 전환하지 않는다")
    void updateProgress_success_doesNotCompleteEnrollmentWhenNotAllCompleted() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        Long resourceId1 = 300L;
        Long resourceId2 = 301L;
        int watchedDuration = 150;
        int totalDuration = 300;

        EnrollmentStatusDto enrollmentStatusDto = new EnrollmentStatusDto(enrollmentId, false, false);
        ProgressUpdateCommand command = new ProgressUpdateCommand(resourceId2, watchedDuration);
        LectureDurationDto lectureDurationDto = new LectureDurationDto(totalDuration);

        Progress progress1 = Progress.of(enrollmentId, resourceId1);
        progress1.updateProgress(totalDuration, totalDuration);
        Progress progress2 = Progress.of(enrollmentId, resourceId2);

        List<LectureSummaryDto> lectureDetails = List.of(
                new LectureSummaryDto(resourceId1, "Lecture1", totalDuration, ResourceType.VIDEO),
                new LectureSummaryDto(resourceId2, "Lecture2", totalDuration, ResourceType.VIDEO)
        );

        given(enrollmentReader.findEnrollment(userId, courseId)).willReturn(Optional.of(enrollmentStatusDto));
        given(lectureProvider.getLectureInfo(resourceId2)).willReturn(lectureDurationDto);
        given(lectureProvider.getLectureDetailsByCourseId(courseId)).willReturn(lectureDetails);
        given(progressRepository.findByEnrollmentIdAndLectureResourceId(enrollmentId, resourceId2)).willReturn(Optional.of(progress2));
        given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(progressRepository.findByEnrollmentId(enrollmentId)).willReturn(List.of(progress1, progress2));

        // when
        progressCommandService.updateProgress(userId, courseId, command);

        // then
        verify(enrollmentCompletionPort, never()).completeEnrollment(enrollmentId);
    }
}

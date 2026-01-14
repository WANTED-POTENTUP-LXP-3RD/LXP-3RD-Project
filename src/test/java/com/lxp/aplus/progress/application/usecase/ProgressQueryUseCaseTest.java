package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.progress.application.port.EnrollmentStatusDto;
import com.lxp.aplus.progress.application.port.EnrollmentReader;
import com.lxp.aplus.progress.application.port.LectureProvider;
import com.lxp.aplus.progress.application.port.LectureSummaryDto;
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
    private ProgressRepository progressRepository;
    @Mock
    private EnrollmentReader enrollmentReader;
    @Mock
    private LectureProvider lectureProvider;

    @Test
    @DisplayName("성공 - 도메인 객체를 통해 학습 이력을 정확히 조회한다")
    void getCourseProgress_success() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        Long enrollmentId = 200L;
        Long resourceId1 = 301L;
        Long resourceId2 = 302L;

        EnrollmentStatusDto enrollmentStatusDto = new EnrollmentStatusDto(enrollmentId, false);
        List<LectureSummaryDto> lectureDetails = List.of(
                new LectureSummaryDto(resourceId1, "Lecture 1", 100),
                new LectureSummaryDto(resourceId2, "Lecture 2", 200)
        );

        Progress progress1 = Progress.of(enrollmentId, resourceId1);
        progress1.updateProgress(100, 100);

        List<Progress> progresses = List.of(progress1);

        given(enrollmentReader.findEnrollment(userId, courseId)).willReturn(Optional.of(enrollmentStatusDto));
        given(lectureProvider.getLectureDetailsByCourseId(courseId)).willReturn(lectureDetails);
        given(progressRepository.findByEnrollmentId(enrollmentId)).willReturn(progresses);

        // when
        CourseProgressResponse response = progressQueryUseCase.getCourseProgress(userId, courseId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEnrollmentId()).isEqualTo(enrollmentId);
        assertThat(response.getOverallProgressRate()).isEqualTo(50);
        assertThat(response.getLastWatchedResourceId()).isEqualTo(resourceId1);
        assertThat(response.getLastWatchedAt()).isNotNull();

        List<LectureProgressResponse> lectureProgresses = response.getLectureProgresses();
        assertThat(lectureProgresses).hasSize(2);

        LectureProgressResponse lp1 = lectureProgresses.get(0);
        assertThat(lp1.getResourceId()).isEqualTo(resourceId1);
        assertThat(lp1.isCompleted()).isTrue();
        assertThat(lp1.getProgressRate()).isEqualTo(100);

        LectureProgressResponse lp2 = lectureProgresses.get(1);
        assertThat(lp2.getResourceId()).isEqualTo(resourceId2);
        assertThat(lp2.isCompleted()).isFalse();
        assertThat(lp2.getProgressRate()).isEqualTo(0);
    }

    @Test
    @DisplayName("실패 - 수강 내역을 찾을 수 없으면 예외를 발생시킨다")
    void getCourseProgress_fail_whenEnrollmentNotFound() {
        // given
        Long userId = 1L;
        Long courseId = 100L;
        given(enrollmentReader.findEnrollment(userId, courseId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> progressQueryUseCase.getCourseProgress(userId, courseId));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }
}
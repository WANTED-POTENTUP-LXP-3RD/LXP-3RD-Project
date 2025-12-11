package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;

import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import com.lxp.aplus.progress.presentation.request.ProgressUpdateRequest;
import com.lxp.aplus.progress.presentation.response.ProgressUpdateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressCommandUseCaseTest {

    @InjectMocks
    private ProgressCommandUseCase progressCommandUseCase; // Corrected type

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository; 

    @Mock
    private LectureResource lectureResource;

    @Mock
    private Progress progress;
    
    @Mock
    private Lecture lecture;

    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        enrollment = Enrollment.builder()
                .id(1L)
                .studentId(10L)
                .courseId(100L)
                .status(EnrollmentStatus.ENROLLED)
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();
        
        when(lectureResource.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("성공 - 기존 학습 이력이 있을 때 진도율을 갱신한다")
    void updateProgress_success_existingProgress() {
        // given
        Long testEnrollmentId = enrollment.getId();
        Long testResourceId = 1L; 
        Integer testWatchedDuration = 150;
        
        given(enrollmentRepository.findById(testEnrollmentId)).willReturn(Optional.of(enrollment));
        given(courseRepository.findAllLecturesWithResourcesByCourseId(enrollment.getCourseId())).willReturn(List.of(lecture));
        when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
        when(lecture.getTotalDurationSeconds()).thenReturn(300);
        given(progressRepository.findByEnrollmentAndLectureResource(enrollment, lectureResource)).willReturn(Optional.of(progress));
        given(progressRepository.save(any(Progress.class))).willReturn(progress);
        when(progress.getEnrollment()).thenReturn(enrollment);
        when(progress.getLectureResource()).thenReturn(lectureResource);
        when(progress.getWatchedDuration()).thenReturn(150);
        when(progress.getLastWatchedAt()).thenReturn(LocalDateTime.now());

        // when
        ProgressUpdateRequest request = new ProgressUpdateRequest(testResourceId, testWatchedDuration);
        ProgressUpdateResponse response = progressCommandUseCase.updateProgress(testEnrollmentId, request);

        // then
        verify(progress).updateProgress(150, false);
        verify(progressRepository).save(progress);
        assertThat(response.lastWatchedDuration()).isEqualTo(150);
        assertThat(response.progressRate()).isEqualTo(50);
    }

    @Test
    @DisplayName("성공 - 새로운 학습 이력을 생성하고 진도율을 갱신한다")
    void updateProgress_success_newProgress() {
        // given
        Long testEnrollmentId = enrollment.getId();
        Long testResourceId = 1L; 
        Integer testWatchedDuration = 60;

        given(enrollmentRepository.findById(testEnrollmentId)).willReturn(Optional.of(enrollment));
        given(courseRepository.findAllLecturesWithResourcesByCourseId(enrollment.getCourseId())).willReturn(List.of(lecture));
        when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
        when(lecture.getTotalDurationSeconds()).thenReturn(300);
        given(progressRepository.findByEnrollmentAndLectureResource(enrollment, lectureResource)).willReturn(Optional.empty());
        
        given(progressRepository.save(any(Progress.class))).willReturn(progress);
        when(progress.getWatchedDuration()).thenReturn(60); 
        when(progress.getEnrollment()).thenReturn(enrollment);
        when(progress.getLectureResource()).thenReturn(lectureResource);
        when(progress.getLastWatchedAt()).thenReturn(LocalDateTime.now());

        // when
        ProgressUpdateRequest request = new ProgressUpdateRequest(testResourceId, testWatchedDuration);
        ProgressUpdateResponse response = progressCommandUseCase.updateProgress(testEnrollmentId, request);

        // then
        verify(progressRepository).save(any(Progress.class));
        assertThat(response.lastWatchedDuration()).isEqualTo(60);
        assertThat(response.progressRate()).isEqualTo(20);
    }
    
    @Test
    @DisplayName("실패 - 수강 내역을 찾을 수 없을 때 예외를 발생시킨다")
    void updateProgress_fail_enrollmentNotFound() {
        // given
        Long testEnrollmentId = 999L;
        Long testResourceId = lectureResource.getId();
        Integer testWatchedDuration = 150;

        given(enrollmentRepository.findById(testEnrollmentId)).willReturn(Optional.empty());

        // when & then
        ProgressUpdateRequest request = new ProgressUpdateRequest(testResourceId, testWatchedDuration);
        BusinessException exception = assertThrows(BusinessException.class, () -> progressCommandUseCase.updateProgress(testEnrollmentId, request));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }

    @Test
    @DisplayName("실패 - 수강 기간이 만료되었을 때 예외를 발생시킨다")
    void updateProgress_fail_enrollmentExpired() {
        // given
        Enrollment expiredEnrollment = Enrollment.builder()
                .id(1L)
                .courseId(100L) 
                .expiredAt(LocalDateTime.now().minusDays(1))
                .build();
        Long testEnrollmentId = expiredEnrollment.getId();
        Long testResourceId = 1L; 
        Integer testWatchedDuration = 150;

        Progress realProgress = Progress.builder()
                .enrollment(expiredEnrollment)
                .lectureResource(lectureResource)
                .build();

        given(enrollmentRepository.findById(testEnrollmentId)).willReturn(Optional.of(expiredEnrollment));
        given(courseRepository.findAllLecturesWithResourcesByCourseId(expiredEnrollment.getCourseId())).willReturn(List.of(lecture));
        when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
        when(lectureResource.getId()).thenReturn(testResourceId);

        when(lecture.getTotalDurationSeconds()).thenReturn(300);
        
        given(progressRepository.findByEnrollmentAndLectureResource(expiredEnrollment, lectureResource)).willReturn(Optional.of(realProgress));

        // when & then
        ProgressUpdateRequest request = new ProgressUpdateRequest(testResourceId, testWatchedDuration);
        BusinessException exception = assertThrows(BusinessException.class, () -> progressCommandUseCase.updateProgress(testEnrollmentId, request));
        assertThat(exception.getErrorCode()).isEqualTo(ProgressErrorCode.CANNOT_UPDATE_EXPIRED_ENROLLMENT);
    }

    @Test
    @DisplayName("실패 - 시청 시간이 영상의 전체 길이를 초과할 때 예외를 발생시킨다")
    void updateProgress_fail_durationExceedsTotal() {
        // given
        Long testEnrollmentId = enrollment.getId();
        Long testResourceId = lectureResource.getId();
        Integer testWatchedDuration = 301;

        given(enrollmentRepository.findById(testEnrollmentId)).willReturn(Optional.of(enrollment));
        given(courseRepository.findAllLecturesWithResourcesByCourseId(enrollment.getCourseId())).willReturn(List.of(lecture));
        when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
        when(lecture.getTotalDurationSeconds()).thenReturn(300);
        when(lectureResource.getId()).thenReturn(1L);
        
        // when & then
        ProgressUpdateRequest request = new ProgressUpdateRequest(testResourceId, testWatchedDuration);
        BusinessException exception = assertThrows(BusinessException.class, () -> progressCommandUseCase.updateProgress(testEnrollmentId, request));
        assertThat(exception.getErrorCode()).isEqualTo(ProgressErrorCode.WATCHED_DURATION_EXCEEDS_TOTAL);
    }
}
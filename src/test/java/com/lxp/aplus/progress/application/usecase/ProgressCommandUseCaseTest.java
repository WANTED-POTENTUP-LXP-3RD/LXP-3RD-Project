package com.lxp.aplus.progress.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import com.lxp.aplus.progress.presentation.request.ProgressUpdateRequest;
import com.lxp.aplus.progress.presentation.response.ProgressUpdateResponse;
import com.lxp.aplus.user.domain.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private ProgressCommandUseCase progressCommandUseCase;

    @Mock
    private ProgressRepository progressRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private Lecture lecture;
    @Mock
    private LectureResource lectureResource;
    
    private UserInfo currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new UserInfo(10L, List.of(RoleType.STUDENT));
    }

    @Test
    @DisplayName("성공 - 미완료 강의를 수강하여 완료 상태로 변경")
    void updateProgress_success_setComplete() {
        // given
        Long enrollmentId = 1L;
        Long resourceId = 1L;
        Long courseId = 100L;

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(currentUser.id()).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
        Progress existingProgress = Progress.of(enrollment, lectureResource);

        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(enrollment));
        given(courseRepository.findAllLecturesWithResourcesByCourseId(courseId)).willReturn(List.of(lecture));
        given(progressRepository.findByEnrollmentAndLectureResource(enrollment, lectureResource)).willReturn(Optional.of(existingProgress));
        
        when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
        when(lectureResource.getId()).thenReturn(resourceId);
        when(lectureResource.getLecture()).thenReturn(lecture);
        when(lecture.getTotalDurationSeconds()).thenReturn(300);

        given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> invocation.getArgument(0));
        
        // when
        ProgressUpdateRequest request = new ProgressUpdateRequest(resourceId, 300);
        progressCommandUseCase.updateProgress(currentUser, enrollmentId, request);

        // then
        ArgumentCaptor<Progress> progressCaptor = ArgumentCaptor.forClass(Progress.class);
        verify(progressRepository).save(progressCaptor.capture());
        Progress savedProgress = progressCaptor.getValue();

        assertThat(savedProgress.isCompleted()).isTrue();
        assertThat(savedProgress.getWatchedDuration()).isEqualTo(300);
    }
    
    @Test
    @DisplayName("성공 - 이미 완료된 강의는 시청시간을 뒤로 돌려도 완료 상태가 유지된다")
    void updateProgress_success_doesNotRevertCompletionStatus() {
        // given
        Long enrollmentId = 1L;
        Long resourceId = 1L;
        Long courseId = 100L;

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(currentUser.id()).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
        
        Progress existingCompletedProgress = Progress.builder()
                .enrollment(enrollment)
                .lectureResource(lectureResource)
                .isCompleted(true)
                .watchedDuration(300)
                .build();

        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(enrollment));
        given(courseRepository.findAllLecturesWithResourcesByCourseId(courseId)).willReturn(List.of(lecture));
        given(progressRepository.findByEnrollmentAndLectureResource(enrollment, lectureResource)).willReturn(Optional.of(existingCompletedProgress));

        when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
        when(lectureResource.getId()).thenReturn(resourceId);
        when(lectureResource.getLecture()).thenReturn(lecture);
        when(lecture.getTotalDurationSeconds()).thenReturn(300);

        given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> invocation.getArgument(0));

        ProgressUpdateRequest request = new ProgressUpdateRequest(resourceId, 100);
        progressCommandUseCase.updateProgress(currentUser, enrollmentId, request);

        ArgumentCaptor<Progress> progressCaptor = ArgumentCaptor.forClass(Progress.class);
        verify(progressRepository).save(progressCaptor.capture());
        Progress savedProgress = progressCaptor.getValue();

        assertThat(savedProgress.isCompleted()).isTrue();
        assertThat(savedProgress.getWatchedDuration()).isEqualTo(100);
    }

    @Test
    @DisplayName("성공 - 새로운 학습 이력을 생성하고 진도율을 갱신한다")
    void updateProgress_success_newProgress() {
        // given
        Long enrollmentId = 1L;
        Long resourceId = 1L;
        Long courseId = 100L;
        
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(currentUser.id()).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
        
        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(enrollment));
        given(courseRepository.findAllLecturesWithResourcesByCourseId(courseId)).willReturn(List.of(lecture));
        given(progressRepository.findByEnrollmentAndLectureResource(enrollment, lectureResource)).willReturn(Optional.empty());

        when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
        when(lectureResource.getId()).thenReturn(resourceId);
        when(lectureResource.getLecture()).thenReturn(lecture);
        when(lecture.getTotalDurationSeconds()).thenReturn(300);
        
        given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> invocation.getArgument(0));
        
        // when
        ProgressUpdateRequest request = new ProgressUpdateRequest(resourceId, 60);
        ProgressUpdateResponse response = progressCommandUseCase.updateProgress(currentUser, enrollmentId, request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.lastWatchedDuration()).isEqualTo(60);
        assertThat(response.progressRate()).isEqualTo(20);
    }
    
    @Test
    @DisplayName("실패 - 수강 내역을 찾을 수 없을 때 예외를 발생시킨다")
    void updateProgress_fail_enrollmentNotFound() {
        // given
        Long testEnrollmentId = 999L;
        ProgressUpdateRequest request = new ProgressUpdateRequest(1L, 150);
        given(enrollmentRepository.findById(testEnrollmentId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> progressCommandUseCase.updateProgress(currentUser, testEnrollmentId, request));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }
    
    @Test
    @DisplayName("실패 - 다른 사용자의 수강 내역에 접근 시 예외를 발생시킨다")
    void updateProgress_fail_noAuthority() {
        // given
        Long enrollmentId = 1L;
        Enrollment othersEnrollment = Enrollment.builder().id(enrollmentId).studentId(999L).build();
        ProgressUpdateRequest request = new ProgressUpdateRequest(1L, 150);

        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(othersEnrollment));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> progressCommandUseCase.updateProgress(currentUser, enrollmentId, request)); // currentUser has ID 10L
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }

    @Test
    @DisplayName("실패 - 수강 기간이 만료되었을 때 예외를 발생시킨다")
    void updateProgress_fail_enrollmentExpired() {
        // given
        Long enrollmentId = 1L;
        Long resourceId = 1L;
        Long courseId = 100L;

        Enrollment expiredEnrollment = Enrollment.builder().id(enrollmentId).studentId(currentUser.id()).courseId(courseId).expiredAt(LocalDateTime.now().minusDays(1)).build();
        Progress existingProgress = Progress.of(expiredEnrollment, lectureResource);

        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(expiredEnrollment));
        given(courseRepository.findAllLecturesWithResourcesByCourseId(courseId)).willReturn(List.of(lecture));
        when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
        when(lectureResource.getId()).thenReturn(resourceId);
        given(progressRepository.findByEnrollmentAndLectureResource(expiredEnrollment, lectureResource)).willReturn(Optional.of(existingProgress));
        
        ProgressUpdateRequest request = new ProgressUpdateRequest(resourceId, 150);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> progressCommandUseCase.updateProgress(currentUser, enrollmentId, request));
        assertThat(exception.getErrorCode()).isEqualTo(ProgressErrorCode.CANNOT_UPDATE_EXPIRED_ENROLLMENT);
    }

    @Test
    @DisplayName("실패 - 시청 시간이 영상의 전체 길이를 초과할 때 예외를 발생시킨다")
    void updateProgress_fail_durationExceedsTotal() {
        // given
        Long enrollmentId = 1L;
        Long resourceId = 1L;
        Long courseId = 100L;

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(currentUser.id()).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
        Progress existingProgress = Progress.of(enrollment, lectureResource);

        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(enrollment));
        given(courseRepository.findAllLecturesWithResourcesByCourseId(courseId)).willReturn(List.of(lecture));
        when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
        when(lectureResource.getId()).thenReturn(resourceId);
        when(lectureResource.getLecture()).thenReturn(lecture);
        when(lecture.getTotalDurationSeconds()).thenReturn(300);
        given(progressRepository.findByEnrollmentAndLectureResource(enrollment, lectureResource)).willReturn(Optional.of(existingProgress));
        
        ProgressUpdateRequest request = new ProgressUpdateRequest(resourceId, 301);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> progressCommandUseCase.updateProgress(currentUser, enrollmentId, request));
                assertThat(exception.getErrorCode()).isEqualTo(ProgressErrorCode.WATCHED_DURATION_EXCEEDS_TOTAL);
            }
        
            @Test
            @DisplayName("성공 - courseId로 진도율을 갱신한다")
            void updateProgressByCourseId_success() {
                // given
                Long resourceId = 1L;
                Long courseId = 100L;
                Long enrollmentId = 1L;
        
                Enrollment enrollment = Enrollment.builder().id(enrollmentId).studentId(currentUser.id()).courseId(courseId).expiredAt(LocalDateTime.now().plusDays(1)).build();
                Progress existingProgress = Progress.of(enrollment, lectureResource);
        
                // Mocking for the new method
                given(enrollmentRepository.findByStudentIdAndCourseId(currentUser.id(), courseId)).willReturn(Optional.of(enrollment));
        
                // Mocking for the original updateProgress method that gets called internally
                given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(enrollment)); 
                given(courseRepository.findAllLecturesWithResourcesByCourseId(courseId)).willReturn(List.of(lecture));
                given(progressRepository.findByEnrollmentAndLectureResource(enrollment, lectureResource)).willReturn(Optional.of(existingProgress));
                
                when(lecture.getLectureResources()).thenReturn(List.of(lectureResource));
                when(lectureResource.getId()).thenReturn(resourceId);
                when(lectureResource.getLecture()).thenReturn(lecture);
                when(lecture.getTotalDurationSeconds()).thenReturn(300);
        
                given(progressRepository.save(any(Progress.class))).willAnswer(invocation -> invocation.getArgument(0));
                
                // when
                ProgressUpdateRequest request = new ProgressUpdateRequest(resourceId, 150);
                ProgressUpdateResponse response = progressCommandUseCase.updateProgressByCourseId(currentUser, courseId, request);
        
                // then
                ArgumentCaptor<Progress> progressCaptor = ArgumentCaptor.forClass(Progress.class);
                verify(progressRepository).save(progressCaptor.capture());
                Progress savedProgress = progressCaptor.getValue();
        
                assertThat(response).isNotNull();
                assertThat(savedProgress.getWatchedDuration()).isEqualTo(150);
                assertThat(response.progressRate()).isEqualTo(50);
            }
        
            @Test
            @DisplayName("실패 - courseId로 갱신 시 수강 내역이 없으면 예외를 발생시킨다")
            void updateProgressByCourseId_fail_enrollmentNotFound() {
                // given
                Long courseId = 999L;
                ProgressUpdateRequest request = new ProgressUpdateRequest(1L, 150);
                given(enrollmentRepository.findByStudentIdAndCourseId(currentUser.id(), courseId)).willReturn(Optional.empty());
        
                // when & then
                BusinessException exception = assertThrows(BusinessException.class, 
                    () -> progressCommandUseCase.updateProgressByCourseId(currentUser, courseId, request));
                assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
            }
        }
        
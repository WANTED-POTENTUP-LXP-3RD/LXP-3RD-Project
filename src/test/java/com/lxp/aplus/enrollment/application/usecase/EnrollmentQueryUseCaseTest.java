package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.application.result.EnrollmentListItemResult;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.progress.domain.ProgressRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EnrollmentQueryUseCaseTest {

    @InjectMocks
    private EnrollmentQueryUseCase enrollmentQueryUseCase;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseFinder courseFinder;

    @Mock
    private ProgressRepository progressRepository;

    private static final Long STUDENT_ID = 1L;
    private static final Long COURSE_ID_1 = 100L;
    private static final Long COURSE_ID_2 = 200L;
    private static final String COURSE_NAME_1 = "Spring Boot 완벽 가이드";
    private static final String COURSE_NAME_2 = "JPA 심화 과정";

    @Test
    @DisplayName("학생의 수강 목록을 상태와 페이지네이션을 적용하여 조회해야 한다.")
    void getEnrollmentList_success() {
        // given
        EnrollmentStatus status = EnrollmentStatus.ENROLLED;
        Pageable pageable = PageRequest.of(0, 10);

        Enrollment enrollment1 = Enrollment.builder()
                .id(5001L)
                .studentId(STUDENT_ID)
                .courseId(COURSE_ID_1)
                .status(EnrollmentStatus.ENROLLED)
                .expiredAt(LocalDateTime.now().plusYears(1))
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .id(5002L)
                .studentId(STUDENT_ID)
                .courseId(COURSE_ID_2)
                .status(EnrollmentStatus.ENROLLED)
                .expiredAt(LocalDateTime.now().plusYears(1).plusMonths(6))
                .build();

        List<Enrollment> enrollments = List.of(enrollment1, enrollment2);
        Page<Enrollment> enrollmentsPage = new PageImpl<>(enrollments, pageable, enrollments.size());

        given(enrollmentRepository.findByStudentIdAndStatus(STUDENT_ID, status, pageable))
                .willReturn(enrollmentsPage);
        given(courseFinder.findCourseById(COURSE_ID_1))
                .willReturn(Optional.of(new CourseSummary(COURSE_ID_1, COURSE_NAME_1)));
        given(courseFinder.findCourseById(COURSE_ID_2))
                .willReturn(Optional.of(new CourseSummary(COURSE_ID_2, COURSE_NAME_2)));
        
        given(courseFinder.countLectures(COURSE_ID_1)).willReturn(10);
        given(progressRepository.countByEnrollmentIdAndIsCompleted(5001L, true)).willReturn(4L);
        given(courseFinder.countLectures(COURSE_ID_2)).willReturn(20);
        given(progressRepository.countByEnrollmentIdAndIsCompleted(5002L, true)).willReturn(7L);

        // when
        Page<EnrollmentListItemResult> result = enrollmentQueryUseCase.getEnrollmentList(STUDENT_ID, status, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        
        assertThat(result.getContent().get(0).progressRate()).isEqualTo(40);
        assertThat(result.getContent().get(1).progressRate()).isEqualTo(35);
    }

    @Test
    @DisplayName("조회된 수강 내역이 없을 경우 빈 목록을 반환해야 한다.")
    void getEnrollmentList_empty() {
        // given
        EnrollmentStatus status = EnrollmentStatus.ENROLLED;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Enrollment> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        given(enrollmentRepository.findByStudentIdAndStatus(STUDENT_ID, status, pageable))
                .willReturn(emptyPage);

        // when
        Page<EnrollmentListItemResult> result = enrollmentQueryUseCase.getEnrollmentList(STUDENT_ID, status, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("성공 - 수강 완료 여부를 true로 반환한다")
    void isEnrollmentCompleted_success_true() {
        // given
        Long enrollmentId = 1L;
        Enrollment completedEnrollment = Enrollment.builder().status(EnrollmentStatus.COMPLETED).build();
        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(completedEnrollment));

        // when
        boolean result = enrollmentQueryUseCase.isEnrollmentCompleted(enrollmentId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("성공 - 수강 완료 여부를 false로 반환한다")
    void isEnrollmentCompleted_success_false() {
        // given
        Long enrollmentId = 1L;
        Enrollment enrolledEnrollment = Enrollment.builder().status(EnrollmentStatus.ENROLLED).build();
        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(enrolledEnrollment));

        // when
        boolean result = enrollmentQueryUseCase.isEnrollmentCompleted(enrollmentId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("실패 - 수강 내역이 없을 때 예외를 던진다")
    void isEnrollmentCompleted_fail_notFound() {
        // given
        Long enrollmentId = 999L;
        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> enrollmentQueryUseCase.isEnrollmentCompleted(enrollmentId));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }

    @Test
    @DisplayName("성공 - 강좌의 수강생 수를 반환한다")
    void getStudentCountForCourse_success() {
        // given
        Long courseId = 1L;
        given(enrollmentRepository.countByCourseId(courseId)).willReturn(5L);

        // when
        long result = enrollmentQueryUseCase.getStudentCountForCourse(courseId);

        // then
        assertThat(result).isEqualTo(5L);
    }
}

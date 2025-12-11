package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.application.port.out.ProgressFinder;
import com.lxp.aplus.enrollment.application.result.EnrollmentListItemResult;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.LongStream;

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
    private ProgressFinder progressFinder;

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

        // Setup for Enrollment 1
        Enrollment enrollment1 = Enrollment.of(STUDENT_ID, COURSE_ID_1, LocalDateTime.now().plusYears(1));
        ReflectionTestUtils.setField(enrollment1, "id", 5001L);

        List<Long> resourceIds1 = LongStream.rangeClosed(1, 10).boxed().toList(); // 10 lectures
        Map<Long, Boolean> completionMap1 = Map.of(1L, true, 2L, true, 3L, true, 4L, true); // 4 completed

        // Setup for Enrollment 2
        Enrollment enrollment2 = Enrollment.of(STUDENT_ID, COURSE_ID_2, LocalDateTime.now().plusYears(1));
        ReflectionTestUtils.setField(enrollment2, "id", 5002L);
        
        List<Long> resourceIds2 = LongStream.rangeClosed(11, 30).boxed().toList(); // 20 lectures
        Map<Long, Boolean> completionMap2 = Map.of(11L, true, 12L, true, 13L, true, 14L, true, 15L, true, 16L, true, 17L, true); // 7 completed

        // Mocking repository to return enrollments
        List<Enrollment> enrollments = List.of(enrollment1, enrollment2);
        Page<Enrollment> enrollmentsPage = new PageImpl<>(enrollments, pageable, enrollments.size());
        given(enrollmentRepository.findByStudentIdAndStatus(STUDENT_ID, status, pageable))
                .willReturn(enrollmentsPage);

        // Mocking course finder for both courses
        given(courseFinder.findCourseById(COURSE_ID_1))
                .willReturn(Optional.of(new CourseSummary(COURSE_ID_1, COURSE_NAME_1)));
        given(courseFinder.findCourseById(COURSE_ID_2))
                .willReturn(Optional.of(new CourseSummary(COURSE_ID_2, COURSE_NAME_2)));

        given(courseFinder.getLectureResourceIds(COURSE_ID_1)).willReturn(resourceIds1);
        given(courseFinder.getLectureResourceIds(COURSE_ID_2)).willReturn(resourceIds2);

        // Mocking progress finder for both enrollments
        given(progressFinder.getCompletionStatusMap(5001L, resourceIds1)).willReturn(completionMap1);
        given(progressFinder.getCompletionStatusMap(5002L, resourceIds2)).willReturn(completionMap2);

        // when
        Page<EnrollmentListItemResult> result = enrollmentQueryUseCase.getEnrollmentList(STUDENT_ID, status, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        
        EnrollmentListItemResult result1 = result.getContent().get(0);
        assertThat(result1.courseId()).isEqualTo(COURSE_ID_1);
        assertThat(result1.progressRate()).isEqualTo(40); // 4 / 10

        EnrollmentListItemResult result2 = result.getContent().get(1);
        assertThat(result2.courseId()).isEqualTo(COURSE_ID_2);
        assertThat(result2.progressRate()).isEqualTo(35); // 7 / 20
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
        Enrollment completedEnrollment = Enrollment.of(1L, 100L, LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(completedEnrollment, "status", EnrollmentStatus.COMPLETED);
        
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
        Enrollment enrolledEnrollment = Enrollment.of(1L, 100L, LocalDateTime.now().plusDays(1)); // status is ENROLLED by default
        
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

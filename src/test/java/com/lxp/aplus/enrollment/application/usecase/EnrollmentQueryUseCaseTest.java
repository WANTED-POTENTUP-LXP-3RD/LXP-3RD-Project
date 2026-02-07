package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.application.port.out.ProgressReader;
import com.lxp.aplus.enrollment.application.result.EnrollmentDetailResult;
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
    private ProgressReader progressReader;

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

        Enrollment enrollment1 = Enrollment.create(STUDENT_ID, COURSE_ID_1, 1L);
        ReflectionTestUtils.setField(enrollment1, "id", 5001L);
        List<String> categories1 = List.of("프로그래밍", "백엔드");

        Enrollment enrollment2 = Enrollment.create(STUDENT_ID, COURSE_ID_2, 1L);
        ReflectionTestUtils.setField(enrollment2, "id", 5002L);
        List<String> categories2 = List.of("프로그래밍", "프론트엔드");

        List<Enrollment> enrollments = List.of(enrollment1, enrollment2);
        Page<Enrollment> enrollmentsPage = new PageImpl<>(enrollments, pageable, enrollments.size());
        given(enrollmentRepository.findByStudentIdAndStatus(STUDENT_ID, status, pageable))
                .willReturn(enrollmentsPage);

        given(courseFinder.findCourseById(COURSE_ID_1))
                .willReturn(Optional.of(new CourseSummary(COURSE_ID_1, COURSE_NAME_1)));
        given(courseFinder.findCourseById(COURSE_ID_2))
                .willReturn(Optional.of(new CourseSummary(COURSE_ID_2, COURSE_NAME_2)));

        given(courseFinder.findCategoryNamesByCourseId(COURSE_ID_1)).willReturn(categories1);
        given(courseFinder.findCategoryNamesByCourseId(COURSE_ID_2)).willReturn(categories2);

        given(progressReader.getOverallProgressRate(5001L, COURSE_ID_1)).willReturn(40);
        given(progressReader.getOverallProgressRate(5002L, COURSE_ID_2)).willReturn(35);

        // when
        Page<EnrollmentListItemResult> result = enrollmentQueryUseCase.getEnrollmentList(STUDENT_ID, status, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        EnrollmentListItemResult result1 = result.getContent().get(0);
        assertThat(result1.courseId()).isEqualTo(COURSE_ID_1);
        assertThat(result1.progressRate()).isEqualTo(40);
        assertThat(result1.categories()).containsExactly("프로그래밍", "백엔드");

        EnrollmentListItemResult result2 = result.getContent().get(1);
        assertThat(result2.courseId()).isEqualTo(COURSE_ID_2);
        assertThat(result2.progressRate()).isEqualTo(35);
        assertThat(result2.categories()).containsExactly("프로그래밍", "프론트엔드");
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

    @Test
    @DisplayName("성공 - 수강 상세 정보를 조회해야 한다.")
    void getEnrollmentDetail_success() {
        // given
        Long studentId = STUDENT_ID;
        Long enrollmentId = 5001L;
        Long courseId = COURSE_ID_1;

        Enrollment enrollment = Enrollment.create(studentId, courseId, 1L);
        ReflectionTestUtils.setField(enrollment, "id", enrollmentId);

        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(enrollment));
        given(progressReader.getOverallProgressRate(enrollmentId, courseId)).willReturn(40);

        // when
        EnrollmentDetailResult result = enrollmentQueryUseCase.getEnrollmentDetail(studentId, enrollmentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.enrollmentId()).isEqualTo(enrollmentId);
        assertThat(result.studentId()).isEqualTo(studentId);
        assertThat(result.courseId()).isEqualTo(courseId);
        assertThat(result.progressRate()).isEqualTo(40);
        assertThat(result.status()).isEqualTo(EnrollmentStatus.ENROLLED);
    }

    @Test
    @DisplayName("실패 - 수강 내역이 없을 때 ENROLLMENT_NOT_FOUND_OR_NO_ACCESS 예외를 던져야 한다.")
    void getEnrollmentDetail_fail_notFound() {
        // given
        Long studentId = STUDENT_ID;
        Long enrollmentId = 9999L;

        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> enrollmentQueryUseCase.getEnrollmentDetail(studentId, enrollmentId));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }

    @Test
    @DisplayName("실패 - 다른 학생의 수강 내역 조회 시 ENROLLMENT_NOT_FOUND_OR_NO_ACCESS 예외를 던져야 한다.")
    void getEnrollmentDetail_fail_accessDenied() {
        // given
        Long requestingStudentId = STUDENT_ID;
        Long actualEnrollmentStudentId = STUDENT_ID + 1;
        Long enrollmentId = 5002L;
        Long courseId = COURSE_ID_2;

        Enrollment enrollment = Enrollment.create(actualEnrollmentStudentId, courseId, 1L);
        ReflectionTestUtils.setField(enrollment, "id", enrollmentId);

        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(enrollment));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> enrollmentQueryUseCase.getEnrollmentDetail(requestingStudentId, enrollmentId));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }

    @Test
    @DisplayName("성공 - courseId로 수강 상세 정보를 조회해야 한다.")
    void getEnrollmentDetailByCourseId_success() {
        // given
        Long studentId = STUDENT_ID;
        Long courseId = COURSE_ID_1;
        Long enrollmentId = 5001L;
        Enrollment enrollment = Enrollment.create(studentId, courseId, 1L);
        ReflectionTestUtils.setField(enrollment, "id", enrollmentId);

        given(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)).willReturn(Optional.of(enrollment));
        given(progressReader.getOverallProgressRate(enrollmentId, courseId)).willReturn(40);

        // when
        EnrollmentDetailResult result = enrollmentQueryUseCase.getEnrollmentDetailByCourseId(studentId, courseId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.enrollmentId()).isEqualTo(enrollmentId);
        assertThat(result.studentId()).isEqualTo(studentId);
        assertThat(result.courseId()).isEqualTo(courseId);
        assertThat(result.progressRate()).isEqualTo(40);
        assertThat(result.status()).isEqualTo(EnrollmentStatus.ENROLLED);
    }

    @Test
    @DisplayName("실패 - courseId로 조회 시 수강 내역이 없으면 ENROLLMENT_NOT_FOUND_OR_NO_ACCESS 예외를 던져야 한다.")
    void getEnrollmentDetailByCourseId_fail_notFound() {
        // given
        Long studentId = STUDENT_ID;
        Long courseId = 9999L;

        given(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> enrollmentQueryUseCase.getEnrollmentDetailByCourseId(studentId, courseId));
        assertThat(exception.getErrorCode()).isEqualTo(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
    }
}

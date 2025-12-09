package com.lxp.aplus.enrollment.application.usecase;

import com.lxp.aplus.enrollment.application.port.out.CourseFinder;
import com.lxp.aplus.enrollment.application.port.out.CourseInfo;
import com.lxp.aplus.enrollment.application.result.EnrollmentListQueryResult;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EnrollmentQueryUseCaseTest {

    @InjectMocks
    private EnrollmentQueryUseCase enrollmentQueryUseCase;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseFinder courseFinder;

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
                .progressRate(45)
                .expiredAt(LocalDateTime.now().plusYears(1))
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .id(5002L)
                .studentId(STUDENT_ID)
                .courseId(COURSE_ID_2)
                .status(EnrollmentStatus.ENROLLED)
                .progressRate(70)
                .expiredAt(LocalDateTime.now().plusYears(1).plusMonths(6))
                .build();

        List<Enrollment> enrollments = List.of(enrollment1, enrollment2);
        Page<Enrollment> enrollmentsPage = new PageImpl<>(enrollments, pageable, enrollments.size());

        given(enrollmentRepository.findByStudentIdAndStatus(STUDENT_ID, status, pageable))
                .willReturn(enrollmentsPage);
        given(courseFinder.findCourseById(COURSE_ID_1))
                .willReturn(Optional.of(new CourseInfo(COURSE_ID_1, COURSE_NAME_1)));
        given(courseFinder.findCourseById(COURSE_ID_2))
                .willReturn(Optional.of(new CourseInfo(COURSE_ID_2, COURSE_NAME_2)));

        // when
        EnrollmentListQueryResult result = enrollmentQueryUseCase.getEnrollmentList(STUDENT_ID, status, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);

        assertThat(result.content().get(0).enrollmentId()).isEqualTo(enrollment1.getId());
        assertThat(result.content().get(0).courseId()).isEqualTo(enrollment1.getCourseId());
        assertThat(result.content().get(0).courseName()).isEqualTo(COURSE_NAME_1);
        assertThat(result.content().get(0).status()).isEqualTo(enrollment1.getStatus());
        assertThat(result.content().get(0).progressRate()).isEqualTo(enrollment1.getProgressRate());

        assertThat(result.content().get(1).enrollmentId()).isEqualTo(enrollment2.getId());
        assertThat(result.content().get(1).courseId()).isEqualTo(enrollment2.getCourseId());
        assertThat(result.content().get(1).courseName()).isEqualTo(COURSE_NAME_2);
        assertThat(result.content().get(1).status()).isEqualTo(enrollment2.getStatus());
        assertThat(result.content().get(1).progressRate()).isEqualTo(enrollment2.getProgressRate());
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
        EnrollmentListQueryResult result = enrollmentQueryUseCase.getEnrollmentList(STUDENT_ID, status, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
    }
}

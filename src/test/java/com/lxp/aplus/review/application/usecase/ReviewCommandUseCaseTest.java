package com.lxp.aplus.review.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.review.application.command.ReviewCreateCommand;
import com.lxp.aplus.review.application.port.out.CourseQueryPort;
import com.lxp.aplus.review.application.port.out.EnrollmentQueryPort;
import com.lxp.aplus.review.application.result.ReviewUpsertResult;
import com.lxp.aplus.review.domain.Review;
import com.lxp.aplus.review.domain.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewCommandUseCaseTest {

    @InjectMocks
    private ReviewCommandUseCase reviewCommandUseCase;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EnrollmentQueryPort enrollmentQueryPort;

    @Mock
    private CourseQueryPort courseQueryPort;

    @Test
    @DisplayName("정상적인 리뷰 생성 성공")
    void createReview_Success() {
        // given
        Long userId = 1L;
        Long courseId = 1L;
        Long instructorId = 2L;
        ReviewCreateCommand command = new ReviewCreateCommand(courseId, userId, 5, "Great content");

        Course course = Course.builder()
                .id(courseId)
                .instructorId(instructorId)
                .build();

        Review savedReview = Review.create(courseId, userId, 5, "Great content");

        given(courseQueryPort.findCourse(courseId)).willReturn(Optional.of(course));
        given(enrollmentQueryPort.existsEnrollment(userId, courseId)).willReturn(true);
        given(reviewRepository.existsOwnReviewInCourse(userId, courseId)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willReturn(savedReview);

        // when
        ReviewUpsertResult result = reviewCommandUseCase.createReview(command);

        // then
        assertThat(result).isNotNull();
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("자신의 강좌에는 리뷰를 남길 수 없음")
    void createReview_Fail_OwnCourse() {
        // given
        Long userId = 1L;
        Long courseId = 1L;
        Long instructorId = 1L; // Same as userId
        ReviewCreateCommand command = new ReviewCreateCommand(courseId, userId, 5, "My course");

        Course course = Course.builder()
                .id(courseId)
                .instructorId(instructorId)
                .build();

        given(courseQueryPort.findCourse(courseId)).willReturn(Optional.of(course));

        // when & then
        assertThatThrownBy(() -> reviewCommandUseCase.createReview(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ReviewErrorCode.CANT_REVIEW_IN_OWN_COURSE);
    }

    @Test
    @DisplayName("수강하지 않은 강좌에는 리뷰를 남길 수 없음")
    void createReview_Fail_NotEnrolled() {
        // given
        Long userId = 1L;
        Long courseId = 1L;
        Long instructorId = 2L;
        ReviewCreateCommand command = new ReviewCreateCommand(courseId, userId, 5, "Not enrolled");

        Course course = Course.builder()
                .id(courseId)
                .instructorId(instructorId)
                .build();

        given(courseQueryPort.findCourse(courseId)).willReturn(Optional.of(course));
        given(enrollmentQueryPort.existsEnrollment(userId, courseId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> reviewCommandUseCase.createReview(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ReviewErrorCode.CANT_REVIEW_IN_NOT_ENROLLED);
    }

    @Test
    @DisplayName("이미 리뷰를 작성한 경우 중복 작성 불가")
    void createReview_Fail_AlreadyReviewed() {
        // given
        Long userId = 1L;
        Long courseId = 1L;
        Long instructorId = 2L;
        ReviewCreateCommand command = new ReviewCreateCommand(courseId, userId, 5, "Again?");

        Course course = Course.builder()
                .id(courseId)
                .instructorId(instructorId)
                .build();

        given(courseQueryPort.findCourse(courseId)).willReturn(Optional.of(course));
        given(enrollmentQueryPort.existsEnrollment(userId, courseId)).willReturn(true);
        given(reviewRepository.existsOwnReviewInCourse(userId, courseId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> reviewCommandUseCase.createReview(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ReviewErrorCode.ALREADY_REGISTER_IN_COURSE);
    }

    @Test
    @DisplayName("존재하지 않는 강좌인 경우 예외 발생")
    void createReview_Fail_CourseNotFound() {
        // given
        Long userId = 1L;
        Long courseId = 999L;
        ReviewCreateCommand command = new ReviewCreateCommand(courseId, userId, 5, "No course");

        given(courseQueryPort.findCourse(courseId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewCommandUseCase.createReview(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CourseErrorCode.COURSE_NOT_FOUND);
    }
}

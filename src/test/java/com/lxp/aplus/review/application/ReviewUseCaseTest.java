package com.lxp.aplus.review.application;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.application.command.ReviewCreateCommand;
import com.lxp.aplus.review.application.command.ReviewUpdateCommand;
import com.lxp.aplus.review.application.policy.ReviewBusinessPolicy;
import com.lxp.aplus.review.application.result.ReviewUpsertResult;
import com.lxp.aplus.review.application.usecase.ReviewCommandUseCase;
import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewCommandUseCase 테스트")
class ReviewUseCaseTest {

    @Mock
    private ReviewBusinessPolicy policy;

    @Mock
    private ReviewsRepository reviewRepository;

    @InjectMocks
    private ReviewCommandUseCase reviewCommandUseCase;

    @Test
    @DisplayName("리뷰를 생성할 수 있다")
    void createReview() {
        // given
        Long courseId = 1L;
        Long userId = 1L;
        Integer rating = 5;
        String content = "좋은 강의입니다.";

        ReviewCreateCommand command = ReviewCreateCommand.builder()
                .courseId(courseId)
                .userId(userId)
                .rating(rating)
                .content(content)
                .build();

        Reviews savedReview = Reviews.create(courseId, userId, rating, content);

        doNothing().when(policy).validateCreateReview(userId, courseId);
        when(reviewRepository.save(any(Reviews.class))).thenReturn(savedReview);

        // when
        ReviewUpsertResult result = reviewCommandUseCase.createReview(command);

        // then
        assertThat(result.rating()).isEqualTo(rating);

        verify(policy, times(1)).validateCreateReview(userId, courseId);
        verify(reviewRepository, times(1)).save(any(Reviews.class));
    }

    @Test
    @DisplayName("리뷰 생성 시 정책 위반 예외가 발생하면 실패한다")
    void createReview_PolicyViolation() {
        // given
        Long courseId = 1L;
        Long userId = 1L;
        ReviewCreateCommand command = ReviewCreateCommand.builder()
                .courseId(courseId)
                .userId(userId)
                .rating(5)
                .content("내용")
                .build();

        doThrow(new BusinessException(ReviewErrorCode.ALREADY_REGISTER_IN_COURSE))
                .when(policy).validateCreateReview(userId, courseId);

        // when & then
        assertThatThrownBy(() -> reviewCommandUseCase.createReview(command))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReviewErrorCode.ALREADY_REGISTER_IN_COURSE);

        verify(reviewRepository, never()).save(any(Reviews.class));
    }

    @Test
    @DisplayName("리뷰를 수정할 수 있다")
    void updateReview() {
        // given
        Long userId = 1L;
        Long reviewId = 100L;
        Long courseId = 10L;
        Integer initialRating = 3;
        String initialContent = "보통입니다.";
        
        Integer updatedRating = 5;
        String updatedContent = "아주 좋습니다.";

        ReviewUpdateCommand command = ReviewUpdateCommand.builder()
                .userId(userId)
                .courseId(courseId)
                .reviewId(reviewId)
                .rating(updatedRating)
                .content(updatedContent)
                .build();

        Reviews existingReview = Reviews.create(courseId, userId, initialRating, initialContent);
        
        when(reviewRepository.getReview(reviewId)).thenReturn(Optional.of(existingReview));
        doNothing().when(policy).validateUpdateReview(userId, existingReview);
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);

        // when
        ReviewUpsertResult result = reviewCommandUseCase.updateReview(command);

        // then
        assertThat(result.rating()).isEqualTo(updatedRating);

        verify(reviewRepository, times(1)).getReview(reviewId);
        verify(policy, times(1)).validateUpdateReview(userId, existingReview);
        verify(reviewRepository, times(1)).save(existingReview);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 수정 시 예외가 발생한다")
    void updateReview_NotFound() {
        // given
        Long reviewId = 999L;
        ReviewUpdateCommand command = ReviewUpdateCommand.builder()
                .reviewId(reviewId)
                .userId(1L)
                .build();

        when(reviewRepository.getReview(reviewId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewCommandUseCase.updateReview(command))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReviewErrorCode.REVIEW_NOT_FOUND);
        
        verify(policy, never()).validateUpdateReview(anyLong(), any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("리뷰 수정 시 정책 위반 예외가 발생하면 실패한다")
    void updateReview_PolicyViolation() {
        // given
        Long userId = 1L;
        Long reviewId = 100L;
        ReviewUpdateCommand command = ReviewUpdateCommand.builder()
                .userId(userId)
                .reviewId(reviewId)
                .rating(5)
                .content("내용")
                .build();

        Reviews existingReview = Reviews.create(1L, userId, 3, "이전 내용");
        
        when(reviewRepository.getReview(reviewId)).thenReturn(Optional.of(existingReview));
        doThrow(new BusinessException(ReviewErrorCode.NOT_OWN_REVIEW))
                .when(policy).validateUpdateReview(userId, existingReview);

        // when & then
        assertThatThrownBy(() -> reviewCommandUseCase.updateReview(command))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReviewErrorCode.NOT_OWN_REVIEW);

        verify(reviewRepository, never()).save(any());
    }
}
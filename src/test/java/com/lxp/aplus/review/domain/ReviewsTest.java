package com.lxp.aplus.review.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewsTest {

    @Test
    @DisplayName("리뷰 생성 시 평점은 2배로 저장된다")
    void create_ShouldScaleRating() {
        // given
        Long courseId = 1L;
        Long userId = 1L;
        Integer rawRating = 5;
        String content = "Great course!";

        // when
        Reviews reviews = Reviews.create(courseId, userId, rawRating, content);

        // then
        assertThat(reviews.getRating()).isEqualTo(10); // 5 * 2
        assertThat(reviews.getCourseId()).isEqualTo(courseId);
        assertThat(reviews.getUserId()).isEqualTo(userId);
        assertThat(reviews.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("리뷰 생성 시 평점 범위 검증 - 최소값 미만")
    void create_ShouldThrowException_WhenRatingIsTooLow() {
        // given
        Long courseId = 1L;
        Long userId = 1L;
        Integer rawRating = 0; // 0 * 2 = 0 < 1
        String content = "Bad course!";

        // when & then
        assertThatThrownBy(() -> Reviews.create(courseId, userId, rawRating, content))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ReviewErrorCode.INVALID_RATING_RANGE);
    }

    @Test
    @DisplayName("리뷰 생성 시 평점 범위 검증 - 최대값 초과")
    void create_ShouldThrowException_WhenRatingIsTooHigh() {
        // given
        Long courseId = 1L;
        Long userId = 1L;
        Integer rawRating = 6; // 6 * 2 = 12 > 10
        String content = "Super course!";

        // when & then
        assertThatThrownBy(() -> Reviews.create(courseId, userId, rawRating, content))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ReviewErrorCode.INVALID_RATING_RANGE);
    }
}

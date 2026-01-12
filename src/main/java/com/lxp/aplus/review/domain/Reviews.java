package com.lxp.aplus.review.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.domain.constant.ReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reviews extends BaseTimeEntity {
    private static final int RATING_SCALE_FACTOR = 2;
    private static final int MIN_RATING_VALUE = 1;
    private static final int MAX_RATING_VALUE = 10;
    private static final String TINY_INT_UNSIGNED = "TINYINT UNSIGNED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = TINY_INT_UNSIGNED)
    private Integer rating;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.DISPLAY;

    private int reported = 0;

    private LocalDateTime deletedAt;

    @Builder
    public Reviews(Long courseId, Long userId, Integer rating, String content) {
        validateRatingRange(rating);
        this.courseId = courseId;
        this.userId = userId;
        this.rating = rating;
        this.content = content;
    }

    public static Reviews create(Long courseId, Long userId, Integer rawRating, String content) {
        int internalRating = rawRating * RATING_SCALE_FACTOR;

        return Reviews.builder()
                .courseId(courseId)
                .userId(userId)
                .rating(internalRating)
                .content(content)
                .build();
    }

    public void update(Integer rating, String content, Long writerId) {
        validateOwnReview(isOwnReview(writerId));
        updateRating(rating);
        updateContent(content);
    }

    private void updateRating(Integer rating) {
        if (rating != null) {
            int internalRating = rating * RATING_SCALE_FACTOR;
            validateRatingRange(internalRating);
            this.rating = internalRating;
        }
    }

    private void updateContent(String content) {
        if (content != null) {
            this.content = content;
        }
    }

    public void blind() {
        this.status = ReviewStatus.BLINDED;
    }

    public void archived() {
        this.status = ReviewStatus.ARCHIVED;
    }

    public void delete(LocalDateTime deletedAt) {
        this.status = ReviewStatus.DELETED;
        this.deletedAt = deletedAt;
    }

    //자신이 작성한 리뷰
    public boolean isOwnReview(Long userId) {
        return userId.equals(this.userId);
    }

    //리뷰 소유자 검증
    public void validateOwnReview(Boolean isOwnReview) {
        if (!isOwnReview) {
            throw new BusinessException(ReviewErrorCode.NOT_OWN_REVIEW);
        }
    }

    private void validateRatingRange(Integer rating) {
        if (rating < MIN_RATING_VALUE || rating > MAX_RATING_VALUE) {
            throw new BusinessException(ReviewErrorCode.INVALID_RATING_RANGE);
        }
    }
}

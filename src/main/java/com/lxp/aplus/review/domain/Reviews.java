package com.lxp.aplus.review.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.domain.constant.ReviewStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reviews extends BaseTimeEntity {
    private static final int RATING_SCALE_FACTOR = 2;
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

    public void update(Integer rating, String content) {
        validateRatingRange(rating);
        this.rating = rating;
        this.content = content;
    }

    public void blind(){
        this.status = ReviewStatus.BLINDED;
    }

    public void archived(){
        this.status = ReviewStatus.ARCHIVED;
    }

    public void delete(LocalDateTime deletedAt) {
        this.status = ReviewStatus.DELETED;
        this.deletedAt = deletedAt;
    }

    private void validateRatingRange(Integer rating) {
        if (rating < 1 || rating > 10) {
            throw new BusinessException(ReviewErrorCode.INVALID_RATING_RANGE);
        }
    }
}

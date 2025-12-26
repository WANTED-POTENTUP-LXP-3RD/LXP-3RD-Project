package com.lxp.aplus.review.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String content;

    private LocalDateTime deletedAt;

    @Builder
    public Review(Long courseId, Long userId, Integer rating, String content) {
        validateRating(rating);
        this.courseId = courseId;
        this.userId = userId;
        this.rating = rating;
        this.content = content;
    }

    public void update(Integer rating, String content) {
        validateRating(rating);
        this.rating = rating;
        this.content = content;
    }

    public void delete(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 10) {
            throw new BusinessException(ReviewErrorCode.INVALID_RATING);
        }
    }
}

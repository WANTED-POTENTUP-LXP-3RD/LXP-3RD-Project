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

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.DISPLAY;

    private LocalDateTime deletedAt;

    @Builder
    public Review(Long courseId, Long userId, Integer rating, String content, ReviewStatus status) {
        validateRating(rating);
        this.courseId = courseId;
        this.userId = userId;
        this.rating = rating;
        this.content = content;
        this.status = status;
    }

    public void update(Integer rating, String content) {
        validateRating(rating);
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

    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 10) {
            throw new BusinessException(ReviewErrorCode.INVALID_RATING);
        }
    }
}

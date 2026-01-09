package com.lxp.aplus.review.application.command;

import com.lxp.aplus.review.domain.Review;
import java.util.Objects;
import lombok.Builder;

@Builder
public record ReviewCreateCommand(
        Long courseId,
        Long userId,
        Integer rating,
        String content
) {
    public Review toEntity() {
        Objects.requireNonNull(this.courseId);
        Objects.requireNonNull(this.userId);

        return Review.create(
                this.courseId,
                this.userId,
                this.rating,
                this.content
        );
    }
}

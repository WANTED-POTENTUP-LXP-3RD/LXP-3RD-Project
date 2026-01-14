package com.lxp.aplus.review.application.command;

import com.lxp.aplus.review.domain.Reviews;
import java.util.Objects;
import lombok.Builder;

@Builder
public record ReviewCreateCommand(
        Long courseId,
        Long userId,
        Integer rating,
        String content
) {
    public Reviews toEntity() {
        Objects.requireNonNull(this.courseId);
        Objects.requireNonNull(this.userId);

        return Reviews.create(
                this.courseId,
                this.userId,
                this.rating,
                this.content
        );
    }
}

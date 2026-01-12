package com.lxp.aplus.review.presentation.request;

import com.lxp.aplus.review.application.command.ReviewUpdateCommand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

public record ReviewUpdateRequest(
        @Min(1)
        @Max(5)
        Integer rating,
        String content
) {
    @Builder
    public ReviewUpdateCommand toCommand(long userId, long courseId, long reviewId) {
        return ReviewUpdateCommand.builder()
                .userId(userId)
                .courseId(courseId)
                .reviewId(reviewId)
                .rating(this.rating)
                .content(this.content)
                .build();
    }
}

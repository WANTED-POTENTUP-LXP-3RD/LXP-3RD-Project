package com.lxp.aplus.review.presentation.request;

import com.lxp.aplus.review.application.command.ReviewCreateCommand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @Min(1)
        @Max(5)
        @NotNull(message = "rating은 필수입니다.")
        Integer rating,
        @NotNull(message = "content는 필수입니다.")
        String content
) {
    public ReviewCreateCommand toCommand(Long userId, Long courseId){
        return ReviewCreateCommand.builder()
                .userId(userId)
                .courseId(courseId)
                .rating(this.rating)
                .content(this.content)
                .build();
    }
}

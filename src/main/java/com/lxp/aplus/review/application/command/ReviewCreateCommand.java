package com.lxp.aplus.review.application.command;

import lombok.Builder;

@Builder
public record ReviewCreateCommand(
        Long courseId,
        Long userId,
        Integer rating,
        String content
) implements BaseReviewCommand {}

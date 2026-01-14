package com.lxp.aplus.review.application.command;

import lombok.Builder;

@Builder
public record ReviewUpdateCommand(
        long userId,
        long courseId,
        Integer rating,
        String content
) {

}

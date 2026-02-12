package com.lxp.aplus.review.application.command;

import lombok.Builder;

public record ReviewQueryCommand(
        Long userId,
        Long courseId,
        int day
) {
    @Builder
    public ReviewQueryCommand {

    }
    public static ReviewQueryCommand of(Long userId, Long courseId) {
        return ReviewQueryCommand.builder()
                .userId(userId)
                .courseId(courseId)
                .build();
    }

    public static ReviewQueryCommand of(Long userId, Long courseId, int day) {
        return new ReviewQueryCommand(userId, courseId, day);
    }
}

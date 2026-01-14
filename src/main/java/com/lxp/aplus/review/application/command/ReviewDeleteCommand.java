package com.lxp.aplus.review.application.command;

public record ReviewDeleteCommand(
        Long userId,
        Long courseId
) {
    public static ReviewDeleteCommand of(Long userId, Long courseId) {
        return new ReviewDeleteCommand(userId, courseId);
    }
}

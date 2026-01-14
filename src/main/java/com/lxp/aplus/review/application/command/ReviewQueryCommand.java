package com.lxp.aplus.review.application.command;

public record ReviewQueryCommand(
        Long userId,
        Long courseId
) {
    public static ReviewQueryCommand of(Long userId, Long courseId) {
        return new ReviewQueryCommand(userId, courseId);
    }
}

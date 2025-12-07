package com.lxp.aplus.enrollment.application.command;

import jakarta.validation.constraints.NotNull;

public record EnrollmentCommand(
        @NotNull String impUid,
        @NotNull String merchantUid,
        @NotNull Long studentId,
        @NotNull Long courseId
) {
}

package com.lxp.aplus.enrollment.presentation.request;

import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull String impUid,
        @NotNull String merchantUid,
        @NotNull Long courseId
) {
    public EnrollmentCommand toCommand(Long studentId) {
        return new EnrollmentCommand(this.impUid, this.merchantUid, studentId, this.courseId);
    }
}

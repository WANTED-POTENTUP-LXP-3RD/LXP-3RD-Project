package com.lxp.aplus.enrollment.presentation.request;

import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull(message = "impUid는 필수입니다.") String impUid,
        @NotNull(message = "merchantUid는 필수입니다.") String merchantUid,
        @NotNull(message = "courseId는 필수입니다.") Long courseId
) {
    public EnrollmentCommand toCommand(Long studentId) {
        return new EnrollmentCommand(this.impUid, this.merchantUid, studentId, this.courseId, null);
    }
}

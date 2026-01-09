package com.lxp.aplus.enrollment.application.command;

import jakarta.validation.constraints.NotNull;

public record EnrollmentCommand(
        @NotNull(message = "impUid는 필수입니다.")
        String impUid,

        @NotNull(message = "merchantUid는 필수입니다.")
        String merchantUid,

        @NotNull(message = "studentId는 필수입니다.")
        Long studentId,

        @NotNull(message = "courseId는 필수입니다.")
        Long courseId,

        Long orderItemId
) {
}

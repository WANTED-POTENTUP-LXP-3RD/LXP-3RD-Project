package com.lxp.aplus.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @NotBlank(message = "새 비밀번호는 필수입니다")
        String newPassword
) {
}


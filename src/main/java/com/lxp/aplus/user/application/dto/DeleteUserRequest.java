package com.lxp.aplus.user.application.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteUserRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId
) {
}


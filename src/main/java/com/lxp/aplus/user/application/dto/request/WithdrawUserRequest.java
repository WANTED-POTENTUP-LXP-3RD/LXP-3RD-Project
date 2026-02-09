package com.lxp.aplus.user.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record WithdrawUserRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId
) {
}


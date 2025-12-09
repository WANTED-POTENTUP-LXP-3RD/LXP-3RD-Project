package com.lxp.aplus.user.application.dto;

import com.lxp.aplus.user.domain.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,
        
        @NotNull(message = "상태는 필수입니다")
        UserStatus status
) {
}


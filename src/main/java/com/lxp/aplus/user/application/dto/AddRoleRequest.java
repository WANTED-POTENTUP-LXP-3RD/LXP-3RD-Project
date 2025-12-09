package com.lxp.aplus.user.application.dto;

import com.lxp.aplus.user.domain.RoleType;
import jakarta.validation.constraints.NotNull;

public record AddRoleRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @NotNull(message = "역할 타입은 필수입니다")
        RoleType roleType
) {
}


package com.lxp.aplus.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserInfoRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @NotBlank(message = "닉네임은 필수입니다")
        String nickName,

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email
) {
}


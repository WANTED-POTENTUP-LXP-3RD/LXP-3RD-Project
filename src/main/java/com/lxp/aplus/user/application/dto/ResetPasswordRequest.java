package com.lxp.aplus.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 비밀번호 재설정 요청 DTO (개발 환경용)
 * 
 * 이메일로 비밀번호를 재설정합니다.
 * 프로덕션에서는 이메일 인증 등을 통해 재설정해야 합니다.
 */
public record ResetPasswordRequest(
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "새 비밀번호는 필수입니다")
        String newPassword
) {
}


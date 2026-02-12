package com.lxp.aplus.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 비밀번호 변경 요청 DTO (Controller용)
 * 
 * /api/users/me/password 엔드포인트에서 사용
 * userId는 Controller에서 인증 정보로부터 가져옴
 */
public record ChangeMyPasswordRequest(
        @NotBlank(message = "새 비밀번호는 필수입니다")
        String newPassword
) {
}



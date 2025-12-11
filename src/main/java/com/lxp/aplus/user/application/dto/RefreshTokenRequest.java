package com.lxp.aplus.user.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 재발급 요청 DTO
 */
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh Token은 필수입니다")
        String refreshToken
) {
}



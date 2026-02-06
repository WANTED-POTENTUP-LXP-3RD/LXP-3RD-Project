package com.lxp.aplus.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 회원정보 수정 요청 DTO (Controller용)
 * 
 * /api/users/me 엔드포인트에서 사용
 * userId는 Controller에서 인증 정보로부터 가져옴
 *
 * 현재는 닉네임만 수정 가능합니다.
 */
public record UpdateMyInfoRequest(
        @NotBlank(message = "닉네임은 필수입니다")
        String nickname
) {
}



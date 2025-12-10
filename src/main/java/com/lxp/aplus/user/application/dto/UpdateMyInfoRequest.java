package com.lxp.aplus.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 회원정보 수정 요청 DTO (Controller용)
 * 
 * /api/users/me 엔드포인트에서 사용
 * userId는 Controller에서 인증 정보로부터 가져옴
 */
public record UpdateMyInfoRequest(
        @NotBlank(message = "닉네임은 필수입니다")
        String nickName,

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email
) {
}



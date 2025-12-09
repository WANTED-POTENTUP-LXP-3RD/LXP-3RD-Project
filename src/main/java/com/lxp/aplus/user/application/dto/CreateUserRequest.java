package com.lxp.aplus.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
        @NotBlank(message = "이름은 필수입니다")
        String name,

        @NotBlank(message = "닉네임은 필수입니다")
        String nickName,

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password,

        @NotBlank(message = "전화번호는 필수입니다")
        @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다 (예: 010-1234-5678)")
        String phoneNumber
) {
}


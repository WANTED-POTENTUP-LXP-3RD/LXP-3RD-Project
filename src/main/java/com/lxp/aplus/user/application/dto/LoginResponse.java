package com.lxp.aplus.user.application.dto;

import com.lxp.aplus.user.domain.RoleType;

import java.util.List;

/**
 * 로그인 응답 DTO
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String nickName,
        List<RoleType> roles
) {
}



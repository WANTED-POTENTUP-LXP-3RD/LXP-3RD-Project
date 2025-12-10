package com.lxp.aplus.user.application.dto;

import com.lxp.aplus.user.domain.RoleType;

import java.util.List;

/**
 * 로그인 응답 DTO
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserInfo user
) {
    /**
     * 사용자 정보 내부 클래스
     */
    public record UserInfo(
            String nickname,
            List<RoleType> roles
    ) {
    }
}



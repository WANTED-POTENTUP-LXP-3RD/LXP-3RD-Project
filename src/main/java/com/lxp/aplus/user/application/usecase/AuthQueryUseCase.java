package com.lxp.aplus.user.application.usecase;

import com.lxp.aplus.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 조회 UseCase
 * 
 * 토큰 검증 등 인증 관련 읽기 작업을 담당합니다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthQueryUseCase {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Access Token에서 사용자 ID 추출
     * 
     * @param accessToken Access Token
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return null;
        }
        return jwtTokenProvider.getUserIdFromToken(accessToken);
    }

    /**
     * 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}


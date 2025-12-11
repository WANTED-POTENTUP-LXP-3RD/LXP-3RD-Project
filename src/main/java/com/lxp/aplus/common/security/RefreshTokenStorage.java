package com.lxp.aplus.common.security;

import com.lxp.aplus.user.application.dto.AuthUser;
import com.lxp.aplus.user.domain.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Refresh Token 저장소
 * 
 * 메모리 기반 저장소로 구현 (프로덕션에서는 Redis 사용 권장)
 * 현재는 ConcurrentHashMap을 사용하여 스레드 안전하게 구현였으나 동일인이 여러 서버 인스턴스를 사용하는 경우 문제가 발생할 수 있음.
 * 추후 분산 캐시(예: Redis)로 변경하여 해결 필요.
 * 
 * TODO: 프로덕션 환경에서는 Redis로 변경 필요
 */
@Slf4j
@Component
public class RefreshTokenStorage {

    private final Map<String, RefreshTokenInfo> tokenStorage = new ConcurrentHashMap<>();

    /**
     * Refresh Token 저장
     * 
     * @param user 사용자 정보
     * @param refreshToken Refresh Token
     * @param expirationTime 만료 시간
     */
    public void save(AuthUser user, String refreshToken, LocalDateTime expirationTime) {
        tokenStorage.put(refreshToken, RefreshTokenInfo.of(user, expirationTime));
        log.debug("Refresh Token 저장: userId={}, expirationTime={}", user.id(), expirationTime);
    }

    /**
     * Refresh Token 조회
     * 
     * @param refreshToken Refresh Token
     * @return 사용자 ID (없으면 null)
     */
    public Long getUserId(String refreshToken) {
        RefreshTokenInfo info = tokenStorage.get(refreshToken);
        if (info == null) {
            return null;
        }
        
        // 만료 확인
        if (info.expirationTime.isBefore(LocalDateTime.now())) {
            tokenStorage.remove(refreshToken);
            return null;
        }
        
        return info.userId;
    }

    /**
     * Refresh Token 삭제
     * 
     * @param refreshToken Refresh Token
     */
    public void delete(String refreshToken) {
        tokenStorage.remove(refreshToken);
        log.debug("Refresh Token 삭제: {}", refreshToken);
    }

    /**
     * 사용자의 모든 Refresh Token 삭제
     * 
     * @param userId 사용자 ID
     */
    public void deleteByUserId(Long userId) {
        tokenStorage.entrySet().removeIf(entry -> entry.getValue().userId.equals(userId));
        log.debug("사용자의 모든 Refresh Token 삭제: userId={}", userId);
    }

    /**
     * Refresh Token 존재 여부 확인
     * 
     * @param refreshToken Refresh Token
     * @return 존재 여부
     */
    public boolean exists(String refreshToken) {
        RefreshTokenInfo info = tokenStorage.get(refreshToken);
        if (info == null) {
            return false;
        }
        
        // 만료 확인
        if (info.expirationTime.isBefore(LocalDateTime.now())) {
            tokenStorage.remove(refreshToken);
            return false;
        }
        
        return true;
    }

    /**
     * Refresh Token 정보를 담는 내부 클래스
     */
    private static class RefreshTokenInfo {
        final Long userId;
        final String userName;
        final String nickName;
        final List<RoleType> roles;
        final LocalDateTime expirationTime;

        RefreshTokenInfo(Long userId, String userName, String nickName, List<RoleType> roles, LocalDateTime expirationTime) {
            this.userId = userId;
            this.userName = userName;
            this.nickName = nickName;
            this.roles = roles;
            this.expirationTime = expirationTime;
        }

        static RefreshTokenInfo of(AuthUser user, LocalDateTime expirationTime) {
            return new RefreshTokenInfo(
                    user.id(),
                    user.email(), // userName 대신 email 사용 (필드명 수정 필요할 수도 있음)
                    user.nickName(),
                    user.roles(),
                    expirationTime
            );
        }
    }
}


package com.lxp.aplus.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Access Token 블랙리스트
 * 
 * 로그아웃된 Access Token을 저장하여 재사용을 방지합니다.
 * 메모리 기반 저장소로 구현 (프로덕션에서는 Redis 사용 권장)
 * 
 * TODO: 프로덕션 환경에서는 Redis로 변경 필요
 */
@Slf4j
@Component
public class AccessTokenBlacklist {

    private final Map<String, LocalDateTime> blacklist = new ConcurrentHashMap<>();

    /**
     * Access Token을 블랙리스트에 추가
     * 
     * @param accessToken Access Token
     * @param expirationTime 토큰 만료 시간
     */
    public void add(String accessToken, LocalDateTime expirationTime) {
        blacklist.put(accessToken, expirationTime);
        log.debug("Access Token 블랙리스트 추가: expirationTime={}", expirationTime);
    }

    /**
     * Access Token이 블랙리스트에 있는지 확인
     * 
     * @param accessToken Access Token
     * @return 블랙리스트 여부
     */
    public boolean contains(String accessToken) {
        LocalDateTime expirationTime = blacklist.get(accessToken);
        if (expirationTime == null) {
            return false;
        }
        
        // 만료된 토큰은 블랙리스트에서 제거
        if (expirationTime.isBefore(LocalDateTime.now())) {
            blacklist.remove(accessToken);
            return false;
        }
        
        return true;
    }

    /**
     * 만료된 토큰 정리 (주기적으로 호출 권장)
     */
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now();
        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
        log.debug("블랙리스트 정리 완료");
    }
}


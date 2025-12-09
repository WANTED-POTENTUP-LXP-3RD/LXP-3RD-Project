package com.lxp.aplus.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityContext에서 인증 정보를 가져오는 유틸리티 클래스
 */
public class SecurityUtils {

    /**
     * 현재 로그인한 사용자 ID를 가져온다.
     * 
     * @return 사용자 ID (인증되지 않은 경우 null)
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        
        return null;
    }

    /**
     * 현재 인증된 사용자가 있는지 확인합니다.
     * 
     * @return 인증 여부
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}


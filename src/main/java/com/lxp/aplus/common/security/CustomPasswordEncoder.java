package com.lxp.aplus.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 비밀번호 암호화 및 검증을 담당하는 컴포넌트
 * 
 * BCrypt 알고리즘을 사용하여 비밀번호를 암호화합니다.
 */
@Slf4j
@Component
public class CustomPasswordEncoder {

    private final PasswordEncoder encoder;

    public CustomPasswordEncoder() {
        this.encoder = new BCryptPasswordEncoder();
    }

    /**
     * 비밀번호 암호화
     * 
     * @param rawPassword 평문 비밀번호
     * @return 암호화된 비밀번호
     */
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 비밀번호 검증
     * 
     * @param rawPassword 평문 비밀번호
     * @param encodedPassword 암호화된 비밀번호 또는 평문 비밀번호
     * @return 일치 여부
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        // null 체크
        if (rawPassword == null || encodedPassword == null) {
            log.debug("비밀번호 또는 암호화된 비밀번호가 null입니다.");
            return false;
        }
        
        // BCrypt 형식인 경우 BCrypt로 검증
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            try {
                return encoder.matches(rawPassword, encodedPassword);
            } catch (Exception e) {
                log.error("BCrypt 비밀번호 검증 중 오류 발생", e);
                return false;
            }
        }
        
        // BCrypt 형식이 아닌 경우 평문 비교 (기존 데이터 호환성)
        return rawPassword.equals(encodedPassword);
    }
}


package com.lxp.aplus.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 Provider
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Access Token 생성
     * 
     * @param userId 사용자 ID
     * @return Access Token
     */
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     * 
     * @param userId 사용자 ID
     * @return Refresh Token
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("type", "refresh")
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰에서 만료 시간 추출
     * 
     * @param token JWT 토큰
     * @return 만료 시간
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 토큰이 만료되었는지 확인
     * 
     * @param token JWT 토큰
     * @return 만료 여부
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 토큰 검증
     * 
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.debug("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 파싱
     * 
     * @param token JWT 토큰
     * @return Claims
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Refresh Token인지 확인
     * 
     * @param token JWT 토큰
     * @return Refresh Token 여부
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Access Token 만료 시간을 초 단위로 반환
     *
     * @return 만료 시간 (초)
     */
    public long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000; // 밀리초를 초로 변환
    }
}


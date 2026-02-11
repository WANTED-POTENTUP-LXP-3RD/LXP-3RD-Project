package com.lxp.aplus.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 토큰을 검증하고 SecurityContext에 인증 정보를 설정하는 필터
 * 
 * Access Token 자동 갱신 로직 포함:
 * - Access Token이 만료되었지만 Refresh Token이 유효한 경우
 * - 새로운 Access Token을 헤더에 추가하여 응답
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStorage refreshTokenStorage;
    private final AccessTokenBlacklist accessTokenBlacklist;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        
        log.debug("JWT 필터 실행: URI={}, 토큰 존재 여부={}", request.getRequestURI(), StringUtils.hasText(token));

        // 토큰이 있는 경우
        if (StringUtils.hasText(token)) {
            // 블랙리스트 확인
            if (accessTokenBlacklist.contains(token)) {
                log.debug("블랙리스트에 등록된 토큰: {}", token);
                filterChain.doFilter(request, response);
                return;
            }
            
            // Access Token 검증
            if (jwtTokenProvider.validateToken(token)) {
                // Access Token이 유효한 경우
                Long userId = jwtTokenProvider.getUserIdFromToken(token);
                log.debug("JWT 토큰 검증 성공: userId={}", userId);
                setAuthentication(userId);
            } else {
                log.debug("JWT 토큰 검증 실패: 토큰이 만료되었거나 유효하지 않음");
                // Access Token이 만료된 경우 - Refresh Token으로 자동 갱신 시도
                boolean refreshed = handleExpiredAccessToken(request, response);
                // Refresh Token이 없거나 유효하지 않은 경우 플래그 설정
                if (!refreshed) {
                    request.setAttribute("TOKEN_EXPIRED", true);
                }
            }
        } else {
            log.debug("JWT 토큰이 없음: URI={}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청에서 토큰 추출
     * 
     * @param request HTTP 요청
     * @return JWT 토큰 (없으면 null)
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenHeader);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix + " ")) {
            return bearerToken.substring(tokenPrefix.length() + 1);
        }
        return null;
    }

    /**
     * SecurityContext에 인증 정보 설정
     * 
     * @param userId 사용자 ID
     */
    private void setAuthentication(Long userId) {
        // TODO: 사용자 권한 정보를 조회하여 설정 (현재는 기본 권한만 설정)
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 만료된 Access Token 처리 및 자동 갱신
     * 
     * Refresh Token을 헤더에서 가져와서 새로운 Access Token을 발급하고
     * 응답 헤더에 추가합니다.
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return Refresh Token으로 갱신 성공 여부
     */
    private boolean handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader("X-Refresh-Token");
        
        if (StringUtils.hasText(refreshToken)) {
            // Refresh Token 검증
            if (jwtTokenProvider.validateToken(refreshToken) && 
                jwtTokenProvider.isRefreshToken(refreshToken)) {
                
                Long userId = refreshTokenStorage.getUserId(refreshToken);
                
                if (userId != null) {
                    // 새로운 Access Token 생성
                    String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
                    
                    // 응답 헤더에 새로운 Access Token 추가
                    response.setHeader("X-New-Access-Token", newAccessToken);
                    
                    // SecurityContext에 인증 정보 설정
                    setAuthentication(userId);
                    
                    log.debug("Access Token 자동 갱신: userId={}", userId);
                    return true;
                }
            }
        }
        return false;
    }
}


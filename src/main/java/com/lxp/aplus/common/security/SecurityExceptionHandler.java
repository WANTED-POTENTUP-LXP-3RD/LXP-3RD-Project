package com.lxp.aplus.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.aplus.common.error.ErrorResponse;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Spring Security 인증/권한 예외 처리 핸들러
 * 
 * 인증 실패(401) 및 권한 부족(403) 시 JSON 형식으로 응답을 반환합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * 인증 실패 시 호출 (401 Unauthorized)
     * 
     * 토큰이 없거나 유효하지 않은 경우
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        
        log.warn("인증 실패: {}", authException.getMessage());
        
        // 만료된 Access Token + Refresh Token 없는 경우
        Boolean tokenExpired = (Boolean) request.getAttribute("TOKEN_EXPIRED");
        GlobalErrorCode errorCode = (tokenExpired != null && tokenExpired) 
                ? GlobalErrorCode.TOKEN_EXPIRED 
                : GlobalErrorCode.UNAUTHORIZED;
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * 권한 부족 시 호출 (403 Forbidden)
     * 
     * 인증은 되었지만 권한이 부족한 경우
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        
        log.warn("권한 부족: {}", accessDeniedException.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(GlobalErrorCode.FORBIDDEN.getStatus())
                .code(GlobalErrorCode.FORBIDDEN.getCode())
                .message(GlobalErrorCode.FORBIDDEN.getMessage())
                .build();
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}


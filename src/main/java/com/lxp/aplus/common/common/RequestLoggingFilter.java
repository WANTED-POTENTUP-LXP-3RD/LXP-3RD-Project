package com.lxp.aplus.common.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 내용을 여러 번 읽을 수 있도록 래핑
        ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        try {
            // 2. 다음 필터/컨트롤러 실행
            filterChain.doFilter(wrappingRequest, wrappingResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 3. 요청/응답 로그 출력
            logRequest(wrappingRequest);
            logResponse(wrappingResponse, duration);

            // 4. 응답 본문을 다시 클라이언트에게 전달 (필수!)
            wrappingResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        
        log.info("============== [HTTP Request] ==============");
        log.info("Method  : {}", request.getMethod());
        log.info("URL     : {}{}", request.getRequestURI(), queryString);
        log.info("Headers : Authorization={}", request.getHeader("Authorization")); // 필요한 헤더만 로깅
        
        // Body 로깅 (JSON인 경우만)
        if (isJson(request.getContentType())) {
            String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            // 너무 긴 바디는 잘라서 출력 (선택사항)
            if (!body.isEmpty()) {
                log.info("Body    : {}", body);
            }
        }
        log.info("============================================");
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        log.info("============= [HTTP Response] ==============");
        log.info("Status  : {}", response.getStatus());
        log.info("Time    : {} ms", duration);
        
        // Body 로깅
        if (isJson(response.getContentType()) && response.getContentAsByteArray().length > 0) {
            String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            log.info("Body    : {}", body);
        }
        log.info("============================================");
    }
    
    private boolean isJson(String contentType) {
        return contentType != null && contentType.contains("application/json");
    }
}

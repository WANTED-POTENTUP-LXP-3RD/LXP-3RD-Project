package com.lxp.aplus.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용 시 불필요)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())            // 반드시 포함!

                // 세션 사용 안 함 (JWT 사용)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // ===== 인증 불필요한 엔드포인트 (Public) =====
                        .requestMatchers("/uploads/**").permitAll()                     // 정적 리소스
                        .requestMatchers("/api/auth/**").permitAll()                    // 인증 관련 API
                        .requestMatchers("/api/internal/v1/analysis/callback").permitAll() // 분석 콜백
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()  // 카테고리 조회
                        .requestMatchers(HttpMethod.GET, "/api/courses", "/api/courses/**").permitAll()  // 강좌 조회
                        
                        // ===== 인증 필요한 엔드포인트 (Protected) =====
                        .requestMatchers("/api/**").authenticated()                     // 나머지 모든 API
                        .anyRequest().authenticated())                                  // 그 외 모든 요청
                
                // 인증/권한 예외 처리
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(securityExceptionHandler) // 401 인증 실패
                        .accessDeniedHandler(securityExceptionHandler)) // 403 권한 부족
                
                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("#{'${cors.allowed-origins}'.split(',')}") List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        
        // 허용할 Origin 설정 (application.yml에서 읽어옴)
        config.setAllowedOrigins(allowedOrigins);
        
        // 허용할 HTTP 메서드 (실제 사용하는 메서드만 명시적으로 허용)
        // GET: 조회 API, POST: 생성 API, PUT: 전체 수정 API, PATCH: 부분 수정 API, DELETE: 삭제 API, OPTIONS: CORS preflight 필수
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // 허용할 헤더 (모든 헤더 허용)
        config.setAllowedHeaders(List.of("*"));
        
        // 인증 정보(쿠키, Authorization 헤더) 허용
        config.setAllowCredentials(true);
        
        // Preflight 요청 캐시 시간 (1시간)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // /api/** 경로에만 CORS 설정 적용
        source.registerCorsConfiguration("/api/**", config);
        // /uploads/** 경로에도 CORS 설정 적용 (정적 리소스)
        source.registerCorsConfiguration("/uploads/**", config);
        
        return source;
    }
}

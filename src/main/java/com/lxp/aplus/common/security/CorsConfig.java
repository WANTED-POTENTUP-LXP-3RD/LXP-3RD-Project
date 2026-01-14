package com.lxp.aplus.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 프론트 주소 허용 (필요한 주소 추가)
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000"
        ));

        // 모든 HTTP 메서드 허용
        config.setAllowedMethods(List.of("*"));

        // 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));

        // 쿠키/Authorization 헤더 사용시 필요
        config.setAllowCredentials(true);

        // preflight 캐시
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
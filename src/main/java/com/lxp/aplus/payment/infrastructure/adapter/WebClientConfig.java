package com.lxp.aplus.payment.infrastructure.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 외부 API 호출용 WebClient 공통 설정
 * - Spring Boot 3.x 기본 HTTP 클라이언트(Reactor Netty) 사용
 * - 타임아웃 필수 설정 (외부 시스템 장애 대비)
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        // ⚠️주의: 실제 운영 환경에서는 타임아웃 설정을 명시적으로 추가하는 것을 권장합니다.
        // 외부 API 통신 실패 시 애플리케이션의 스레드가 무한정 대기하는 것을 방지합니다.

        /*
        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(5)) // 응답 타임아웃 5초 설정
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000); // 연결 타임아웃 3초 설정

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        */

        return builder
                // .clientConnector(connector) // 타임아웃 설정을 포함할 경우 주석 해제
                .build();
    }
}

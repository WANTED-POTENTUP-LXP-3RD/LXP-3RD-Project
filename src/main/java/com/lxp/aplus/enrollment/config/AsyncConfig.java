package com.lxp.aplus.enrollment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring의 @Async 어노테이션을 활성화하여 비동기 메서드 실행을 지원.
 * 이벤트 리스너 등을 비동기로 처리하여 주요 트랜잭션의 응답 시간을 단축시킬 수 있습니다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}

package com.lxp.aplus.enrollment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Spring의 @Retryable, @Recover 어노테이션을 활성화하여 재시도 로직을 지원.
 * 일시적인 오류(예: DB 커넥션 실패)에 대해 자동으로 재시도를 수행하여 시스템의 안정성을 높입니다.
 */
@Configuration
@EnableRetry
public class RetryConfig {
}

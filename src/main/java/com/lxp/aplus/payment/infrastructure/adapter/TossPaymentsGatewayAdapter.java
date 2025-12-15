package com.lxp.aplus.payment.infrastructure.adapter;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.PaymentErrorCode;
import com.lxp.aplus.payment.application.port.out.PaymentGatewayPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TossPaymentsGatewayAdapter implements PaymentGatewayPort {

    private final String secretKey;
    private final String baseUrl;
    private final WebClient webClient;

    protected TossPaymentsGatewayAdapter(
            @Value("${pg.toss.secret-key}") String secretKey,
            @Value("${pg.toss.base-url}") String baseUrl,
            WebClient webClient
    ) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.webClient = webClient;
    }

    @Override
    public void confirmPayment(String orderId, String paymentKey, BigDecimal amount) {

        // 승인 API 호출
        TossPaymentsConfirmRequest requestBody = TossPaymentsConfirmRequest.of(orderId, paymentKey, amount);

        webClient.post()
                .uri(this.baseUrl + "/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .doOnNext(body ->
                                        System.out.println("🔥 Toss 4xx error body = " + body)
                                )
                                .then(Mono.error(
                                        new BusinessException(PaymentErrorCode.PAYMENT_CONFIRM_ERROR)
                                ))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .doOnNext(body ->
                                        System.out.println("🔥 Toss 5xx error body = " + body)
                                )
                                .then(Mono.error(
                                        new BusinessException(PaymentErrorCode.PAYMENT_GATEWAY_ERROR)
                                ))
                )
                .toBodilessEntity()
                .block();
    }

    /*
     * 인증 헤더 생성
     * - 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
     * - 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
     */
    private String createAuthorizationHeader() {
        String credential = this.secretKey + ":";
        String encodedCredential = Base64.getEncoder()
                .encodeToString(credential.getBytes(StandardCharsets.UTF_8));
        System.out.println("✅ Basic " + encodedCredential);

        return "Basic " + encodedCredential;
    }
}

package com.lxp.aplus.payment.application.usecase;

import com.lxp.aplus.order.domain.OrderRepository;
import com.lxp.aplus.payment.application.command.PreparePaymentCommand;
import com.lxp.aplus.payment.application.result.PaymentResult;
import com.lxp.aplus.payment.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentCommandUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public PaymentResult prepare(PreparePaymentCommand command) {
        // Order 조회
        // Payment 생성
        // 저장
        // Result 반환
    }

    /*
    @Override
    public void confirm(ConfirmPaymentCommand command) {
        // Payment 조회
        // 승인
        // Order 상태 변경
    }
    */
}

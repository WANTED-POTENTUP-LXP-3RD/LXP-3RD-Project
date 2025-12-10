package com.lxp.aplus.payment.application.usecase;

import com.lxp.aplus.order.application.result.OrderCreationResult;
import com.lxp.aplus.order.application.usecase.OrderCommandUseCase;
import com.lxp.aplus.payment.application.command.PreparePaymentCommand;
import com.lxp.aplus.payment.application.result.PaymentResult;
import com.lxp.aplus.payment.domain.Payment;
import com.lxp.aplus.payment.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderCommandUseCase orderCommandUseCase; // FIXME: Order BC 침범 (의도됨) ⚠️

    public PaymentResult prepare(PreparePaymentCommand command) {

        // 1. Order 생성 (From Order BC)
        OrderCreationResult orderResult = orderCommandUseCase.createOrderFromCourseIds(
                command.userId(),
                command.courseIds()
        );

        // 2. Payment 생성
        Payment payment = Payment.create(
                orderResult.orderId(),
                command.userId(),
                orderResult.amount()
        );
        paymentRepository.save(payment);

        // 3. 결과 반환
        return PaymentResult.from(payment);
    }
}

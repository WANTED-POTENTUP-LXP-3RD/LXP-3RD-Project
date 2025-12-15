package com.lxp.aplus.payment.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.PaymentErrorCode;
import com.lxp.aplus.payment.application.port.out.EnrollmentCommandPort;
import com.lxp.aplus.payment.application.port.out.OrderQueryPort;
import com.lxp.aplus.payment.application.port.out.PaymentGatewayPort;
import com.lxp.aplus.payment.application.result.OrderCreateResult;
import com.lxp.aplus.payment.application.command.PaymentConfirmCommand;
import com.lxp.aplus.payment.application.command.PaymentPrepareCommand;
import com.lxp.aplus.payment.application.port.out.OrderCommandPort;
import com.lxp.aplus.payment.domain.Payment;
import com.lxp.aplus.payment.domain.PaymentRepository;
import com.lxp.aplus.payment.presentation.response.PaymentPrepareResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderCommandPort orderCommandPort;
    private final OrderQueryPort orderQueryPort;
    private final EnrollmentCommandPort enrollmentCommandPort;
    private final PaymentGatewayPort paymentGatewayPort;

    public PaymentPrepareResponse prepare(PaymentPrepareCommand command) {

        // 1. Order 생성 (From Order BC)
        OrderCreateResult orderResult = orderCommandPort.createOrderFromCourseIds(
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
        return PaymentPrepareResponse.from(payment);
    }

    public void confirm(PaymentConfirmCommand command) {
        // 1. Payment 조회
        Payment payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 2. PG사에 최종 승인 요청
        paymentGatewayPort.confirmPayment(command.orderId(), command.paymentKey(), command.amount());

        // 3. 도메인 불변성 검증 -> 상태 변경
        payment.approve(command.paymentKey(), command.amount());

        // 4. Order BC에 완료 통보 (결제 성공 후 Order 상태를 COMPLETED로 변경 요청)
        orderCommandPort.completeOrder(command.orderId(), payment.getPaymentId(), command.amount());

        // 5. Payment 저장
        paymentRepository.save(payment);

        List<Long> courseIds = orderQueryPort.getCourseIdsByOrderId(command.orderId());

        // 6. 수강 권한 부여
        // TODO: 향후 이벤트로 처리
        enrollmentCommandPort.enrollUserInCourses(command.userId(), courseIds);
    }
}

package com.lxp.aplus.order.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
//import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseAggregateRoot {

    @Id
    @Column(name = "id")
    private String orderId;

    //@Column
    //private Long userId;

    @Column(nullable = false)
    private BigDecimal amount;

    //@Column(nullable = false)
    //private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column
    private String approvedPaymentId; // orderStatus = COMPLETED일 때만 존재

    //@Column
    //private String cancelReason;
    //
    //@Column(nullable = false, updatable = false)
    //private LocalDateTime orderedAt;
    //
    //@Column
    //private LocalDateTime completedAt;



    private Order(
            String orderId,
            BigDecimal amount,
            OrderStatus orderStatus
    ) {
        this.orderId = orderId;
        this.amount = amount;
        this.orderStatus = orderStatus;
    }

    /* ========= 생성 ========= */

    /*
     * 주문 생성
     * - Order는 항상 PENDING 상태로만 생성된다.
     */
    public static Order create(BigDecimal amount) {
        String orderId = UUID.randomUUID().toString();  // TODO: 규칙 만들기

        return new Order(
                orderId,
                amount,
                OrderStatus.PENDING
        );
    }

    /* ========= 도메인 행위 ========= */

    /*
     * 결제 승인 -> 주문 완료
     * - PENDING 상태에서만 수행 가능
     * - 하나의 Order에는 승인된 Payment가 하나만 존재
     * - 승인된 결제 금액 = 주문 금액
     *
     * TODO: Payment Approved 이벤트릐 결과를 반영하도록 수정
     */
    public void completeWithApprovedPayment(
            String paymentId,
            BigDecimal approvedAmount
    ) {
        validatePending();
        validateNoApprovedPayment();
        validateAmount(approvedAmount);

        this.orderStatus = OrderStatus.COMPLETED;
        this.approvedPaymentId = paymentId;
    }

    /*
     * 주문 취소
     * - PENDING 상태에서만 취소 가능
     * - COMPLETED → CANCELED로 직접 변경 불가 (= Payment BC의 책임)
     */
    public void cancel() {
        validatePending();
        this.orderStatus = OrderStatus.CANCELED;
    }

    /* ========= 검증 ========= */

    /*
     * Order가 PENDING 상태인지 확인
     */
    private void validatePending() {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalStateException("Pending 상태에서만 수행 가능합니다.");
        }
    }

    /*
     * 결제 중복 승인 방지
     */
    private void validateNoApprovedPayment() {
        if (this.approvedPaymentId != null) {
            throw new IllegalStateException("이미 승인된 결제가 존재합니다.");
        }
    }

    /*
     * 금액 정합성 검증
     */
    private void validateAmount(BigDecimal approvedAmount) {
        if (!this.amount.equals(approvedAmount)) {
            throw new IllegalArgumentException("Order 금액과 승인 금액이 일치하지 않습니다.");
        }
    }
}

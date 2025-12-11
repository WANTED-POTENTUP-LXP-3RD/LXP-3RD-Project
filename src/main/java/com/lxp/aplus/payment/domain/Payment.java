package com.lxp.aplus.payment.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// TODO: amount, currency 묶어서 VO(Money)로 만들기
@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_key", columnNames = "payment_key")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseAggregateRoot {

    @Id
    @Column(name = "id")
    private String paymentId;  // 외부 시스템(운영/정산/CS)에 노출 가능

    // NOTE: Aggregate 간 연관은 ID 참조 수준으로만 둡니다.
    @Column(nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false, updatable = false)
    private String orderId;

    @Column(unique = true, updatable = false)
    private String paymentKey; // PG transactionId

    @Column(nullable = false, updatable = false, length = 3)
    private String currency;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private PgProvider pgProvider;

    @Column
    private LocalDateTime approvedAt;

    @Column
    private LocalDateTime canceledAt;

    @Column
    private LocalDateTime refundedAt;

    private Payment(
            String paymentId,
            Long userId,
            String orderId,
            BigDecimal amount
    ) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = "KRW";
        this.paymentMethod = PaymentMethod.CARD;
        this.pgProvider = PgProvider.TOSS;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    /* ========= 생성 ========= */

    /*
     * 결제 생성
     * - Payment는 항상 PENDING 상태로만 생성된다.
     * - 금액(amount) 변경 불가
     */
    public static Payment create(String orderId, Long userId, BigDecimal amount) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId is null");
        }

        String paymentId = UUID.randomUUID().toString();  // TODO: 규칙 만들기

        return new Payment(
                paymentId,
                userId,
                orderId,
                amount
        );
    }


    /* ========= 도메인 행위 ========= */

    /**
     * 결제 승인
     * - Payment amount는 승인된 금액과 일치해야 한다
     * - 이미 처리된 paymentKey로 다시 승인할 수 없다.
     * - PENDING -> APPROVED
     */
    public void approve(
            String paymentKeyFromPG,
            BigDecimal approvedAmount
    ) {
        validatePending();
        validateAmount(approvedAmount);
        validatePaymentKeyNotAssigned();

        this.paymentStatus = PaymentStatus.APPROVED;
        this.paymentKey = paymentKeyFromPG;
        this.approvedAt = LocalDateTime.now();
    }

    /*
     * 결제 실패
     * - PENDING 상테에서만 가능
     * - PENDING -> FAILED
     */
    public void fail() {
        validatePending();
        this.paymentStatus = PaymentStatus.FAILED;
    }

    /*
     * 결제 취소 (카드사 매입 전)
     * - APPROVED 상태에서만 가능
     * - 결과: 상태=CANCELED
     */
    public void cancel() {
        validateApproved();
        this.paymentStatus = PaymentStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }

    /*
     * 결제 환불 (카드사 매입 후)
     * - APPROVED 상태에서만 가능
     * - 결과: 상태=REFUNDED
     */
    public void refund() {
        validateApproved();
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.refundedAt = LocalDateTime.now();
    }


    /* ========= 검증 ========= */

    /*
     * Payment가 PENDING 상태인지 확인
     */
    private void validatePending() {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalStateException("Pending 상태에서만 수행할 수 있습니다.");
        }
    }

    /*
     * Payment가 APPROVED 상태인지 확인
     */
    private void validateApproved() {
        if (this.paymentStatus != PaymentStatus.APPROVED) {
            throw new IllegalStateException("Approved 상태에서만 수행할 수 있습니다.");
        }
    }

    /*
     * 승인된 금액과 Payment.amount가 동일한지 확인
     */
    private void validateAmount(BigDecimal approvedAmount) {
        if (!this.amount.equals(approvedAmount)) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
    }

    /*
     * 이미 paymentKey가 할당된 경우 중복 승인 방지
     */
    private void validatePaymentKeyNotAssigned() {
        if (this.paymentKey != null) {
            throw new IllegalStateException("이미 승인 처리된 결제입니다.");
        }
    }
}

package com.lxp.aplus.order.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO: amount, currency 묶어서 VO(Money)로 만들기
// TODO: 특정 상태에 종속적인 필드를 관리하는 구조적인 방법 고민(approvedPaymentId, cancelReason)
// TODO: custom exception 적용하기
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseAggregateRoot {

    @Id
    @Column(name = "id")
    private String orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal amount;

    @ElementCollection
    @CollectionTable(
            name = "order_lines",
            joinColumns = @JoinColumn(name = "order_id")
    )
    private List<OrderLine> orderLines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'PENDING'")
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column
    private String approvedPaymentId; // orderStatus = COMPLETED일 때만 존재

    @Column
    private String cancelReason;

    @Column
    private LocalDateTime completedAt;

    private Order(
            String orderId,
            Long userId,
            BigDecimal amount,
            List<OrderLine> orderLines
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.currency = "KRW";
        this.amount = amount;
        this.orderLines = orderLines;
    }

    /* ========= 생성 ========= */

    /*
     * 주문 생성
     * - Order는 항상 PENDING 상태로만 생성된다.
     */
    public static Order create(
            Long userId,
            BigDecimal amount,
            List<OrderLine> orderLines
    ) {
        String orderId = UUID.randomUUID().toString();  // TODO: 규칙 만들기

        return new Order(
                orderId,
                userId,
                amount,
                new ArrayList<>(orderLines)
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
        this.completedAt = LocalDateTime.now();
    }

    /*
     * 주문 취소
     * - PENDING 상태에서만 취소 가능
     * - COMPLETED → CANCELED로 직접 변경 불가 (= Payment BC의 책임)
     */
    public void cancel(String reason) {
        validatePending();
        this.orderStatus = OrderStatus.CANCELED;
        this.cancelReason = reason;
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

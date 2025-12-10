package com.lxp.aplus.order.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * OrderLine (Value Object)
 *
 * 주문에 포함된 "주문 항목"을 표현하는 값 객체(Value Object)이다.
 *
 * [설계 의도]
 * - Order가 "무엇을 주문했는지"를 표현하기 위한 구성 요소
 * - 단건 결제 / 묶음 결제 확장을 고려한 주문 항목 모델
 *
 * [DDD 관점]
 * - 고유 식별자(ID)를 가지지 않는다
 * - Order Aggregate에 종속된 Value Object이다
 * - Order 외부에서는 독립적으로 존재하거나 사용될 수 없다
 *
 * [정합성 규칙]
 * - OrderLine은 Order와 생명주기를 함께한다
 * - OrderLine 단독 변경/조회/삭제는 허용하지 않는다
 *
 * [영속성 전략]
 * - JPA @Embeddable + @ElementCollection 으로 매핑된다
 * - 별도의 테이블(order_lines)에 저장되지만 식별자는 없다
 *
 * ⚠️ 주의
 * - 향후 주문 항목 단위의 상태 관리/취소/환불이 필요해질 경우
 *   Entity(OrderItem)로의 승격을 검토한다
 */

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLine {

    private ItemType itemType;
    private Long itemId;
    private BigDecimal price;

    protected OrderLine(ItemType itemType, Long itemId, BigDecimal price) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.price = price;
    }

    // FIXME: course 네이밍 바꾸기 (course 도메인과 헷갈릴 여지 있음)
    public static OrderLine course(Long courseId, BigDecimal price) {
        return new OrderLine(ItemType.COURSE, courseId, price);
    }
}

package com.lxp.aplus.enrollment.application.event;

/**
 * 수강 취소 시 발행되는 이벤트입니다.
 *
 * @param orderItemId 취소된 수강과 연결된 주문 항목 ID
 */
public record EnrollmentCanceledEvent(
        Long orderItemId
) {
}

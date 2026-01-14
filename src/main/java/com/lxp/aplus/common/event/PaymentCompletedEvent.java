package com.lxp.aplus.common.event;

/**
 * 결제가 성공적으로 완료되었을 때 발행되는 도메인 이벤트입니다.
 * 이 이벤트는 결제 트랜잭션이 성공적으로 커밋된 후 발행되어야 합니다.
 *
 * <p>
 * <strong>[MSA 전환 가이드]</strong>
 * <p>
 * 현재 이 이벤트 클래스는 'common' 모듈에 위치하여 여러 도메인(모듈)이 공유하고 있다.
 * 이는 모놀리식 아키텍처에서는 순환 참조를 방지하고 코드를 재사용하는 일반적인 방법.
 * <p>
 * 하지만 마이크로서비스 아키텍처(MSA)로 전환될 경우, 이와 같은 공유는 서비스 간의 강한 결합(tight coupling)을 유발하여
 * '분산된 모놀리스' 문제를 야기할 수 있다. (예: 이 클래스의 변경이 모든 서비스를 재배포하게 만듦)
 * <p>
 * 따라서 MSA로 전환 시에는 다음과 같은 방식으로 리팩토링되어야 합니다.
 * <ol>
 *   <li><b>이벤트 클래스의 독립적 정의</b>: `Payment` 서비스와 `Enrollment` 서비스는 이 클래스를 공유하는 대신, 각자 자신의 경계 내에서 이벤트를 표현하는 클래스를 독립적으로 정의. (예: `payment.event.PaymentCompleted`, `enrollment.event.PaymentCompleted`)</li>
 *   <li><b>메시지 브로커(Message Broker) 도입</b>: 서비스 간의 통신은 Spring의 동기식 `ApplicationEventPublisher` 대신, Kafka, RabbitMQ 등과 같은 메시지 브로커를 통해 비동기 메시지로 전달.</li>
 *   <li><b>스키마(Schema) 기반 통신</b>: 두 서비스는 Java 클래스를 직접 공유하는 대신, 약속된 JSON 또는 Avro 같은 메시지 '스키마(구조)'에 따라서만 통신합니다. 각 서비스의 이벤트 클래스는 이 스키마를 만족하기만 하면 됩니다.</li>
 * </ol>
 *
 * @param studentId   수강생 ID
 * @param orderItemId 주문 항목 ID (어떤 주문에 속한 것인지 식별)
 * @param courseId    수강 신청할 강의 ID
 */
public record PaymentCompletedEvent(
    Long studentId,
    Long orderItemId,
    Long courseId
) {
}

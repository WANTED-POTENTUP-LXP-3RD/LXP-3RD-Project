package com.lxp.aplus.common.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@MappedSuperclass
public abstract class BaseAggregateRoot extends BaseTimeEntity {

    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    protected <T> void registerEvent(T event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    /**
     * [이벤트 발행 메서드]
     * Repository의 save() 등이 호출될 때, Spring Data JPA가 자동으로 이 메서드를 호출함
     * 리턴된 이벤트 목록을 ApplicationEventPublisher를 통해 발행(Publish)
     */
    @DomainEvents
    protected Collection<Object> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * [이벤트 초기화 메서드]
     * 이벤트 발행이 완료된 후, Spring Data JPA가 자동으로 호출함
     * 이미 발행된 이벤트가 리스트에 남아있지 않도록 비워줌
     */
    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }
}

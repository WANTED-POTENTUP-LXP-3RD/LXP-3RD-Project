package com.lxp.aplus.enrollment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EnrollmentStatus {
    ENROLLED("수강 중"),
    COMPLETED("수강 완료"),
    CANCELED("수강 취소"),
    EXPIRED("수강 기간 만료");

    @Getter
    private final String description;

    public boolean isActive() {
        return this == ENROLLED || this == COMPLETED;
    }
}

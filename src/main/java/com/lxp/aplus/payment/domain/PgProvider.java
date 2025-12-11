package com.lxp.aplus.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PgProvider {
    TOSS("토스페이먼츠");

    private final String description;
}

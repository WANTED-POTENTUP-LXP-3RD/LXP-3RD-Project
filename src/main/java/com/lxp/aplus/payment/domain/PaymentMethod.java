package com.lxp.aplus.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CARD("카드");
    /*
    NAVER_PAY("네이버페이"),
    KAKAO_PAY("카카오페이"),
    TOSS_PAY("토스페이"),
    BANK_TRANSFER("계좌이체"),
    */

    private final String description;
}

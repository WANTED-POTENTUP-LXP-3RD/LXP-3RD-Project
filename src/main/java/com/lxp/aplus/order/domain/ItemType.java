package com.lxp.aplus.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemType {
    COURSE("강좌");

    private final String description;
}

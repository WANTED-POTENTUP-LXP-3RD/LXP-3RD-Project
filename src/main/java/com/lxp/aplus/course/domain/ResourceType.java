package com.lxp.aplus.course.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ResourceType {
    VIDEO("영상"),
    PDF("PDF 자료");

    @Getter
    private final String displayName;
}

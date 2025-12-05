package com.lxp.aplus.course.domain;

import lombok.Getter;

@Getter
public enum ResourceType {
    VIDEO("video"),
    PDF("pdf"),
    QUIZ("quiz");

    private final String type;

    ResourceType(String type) {
        this.type = type;
    }
}

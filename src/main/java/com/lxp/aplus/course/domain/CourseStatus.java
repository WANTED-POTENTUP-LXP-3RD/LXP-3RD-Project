package com.lxp.aplus.course.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseStatus {
    DRAFT("작성중"),
    PUBLISHED("발행됨"),
    DELETED("삭제됨");

    private final String description;

    public String getDescription() {
        return description;
    }
}

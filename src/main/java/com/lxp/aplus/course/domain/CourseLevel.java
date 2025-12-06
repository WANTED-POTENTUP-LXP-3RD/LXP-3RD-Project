package com.lxp.aplus.course.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CourseLevel {
    BEGINNER("초급"),
    INTERMEDIATE("중급"),
    ADVANCED("고급");

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }
}

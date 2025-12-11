package com.lxp.aplus.course.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CourseLevel {
    BEGINNER("입문"),
    NOVICE("초급"),
    INTERMEDIATE("중급"),
    ADVANCED("고급");

    @Getter
    private final String displayName;
}

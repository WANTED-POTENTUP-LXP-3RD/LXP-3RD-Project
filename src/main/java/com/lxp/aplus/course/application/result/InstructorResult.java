package com.lxp.aplus.course.application.result;

import lombok.Builder;

@Builder
public record InstructorResult(
        Long id,
        String nickName
) {
}

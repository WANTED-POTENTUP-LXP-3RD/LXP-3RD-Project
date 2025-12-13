package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.InstructorResult;
import com.lxp.aplus.user.domain.User;
import lombok.Builder;

@Builder
public record InstructorResponse(
        Long id,
        String name
) {
    public static InstructorResponse from(InstructorResult result) {
        return InstructorResponse.builder()
                .id(result.id())
                .name(result.name())
                .build();
    }
}

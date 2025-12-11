package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.user.domain.User;
import lombok.Builder;

@Builder
public record InstructorResponse(
        Long id,
        String name
) {
    public static InstructorResponse from(User user) {
        return InstructorResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}

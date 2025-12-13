package com.lxp.aplus.course.application.result;

import com.lxp.aplus.user.domain.User;
import lombok.Builder;

@Builder
public record InstructorResult(
        Long id,
        String name
) {
    public static InstructorResult from(User user) {
        return InstructorResult.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}

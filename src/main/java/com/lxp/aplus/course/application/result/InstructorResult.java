package com.lxp.aplus.course.application.result;

import com.lxp.aplus.user.domain.User;
import lombok.Builder;

@Builder
public record InstructorResult(
        Long id,
        String nickName
) {
    public static InstructorResult from(User user) {
        return InstructorResult.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .build();
    }
}

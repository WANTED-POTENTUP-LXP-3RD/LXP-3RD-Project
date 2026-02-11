package com.lxp.aplus.course.application.port.out.dto;

import com.lxp.aplus.user.application.internal.dto.UserInternalResult;
import lombok.Builder;

@Builder
public record CourseInstructorResult(
        Long id,
        String nickName
) {
    public static CourseInstructorResult from(UserInternalResult dto) {
        return CourseInstructorResult.builder()
                .id(dto.id())
                .nickName(dto.nickName())
                .build();
    }
}

package com.lxp.aplus.course.application.port.out.dto;

import com.lxp.aplus.course.application.result.InstructorResult;
import com.lxp.aplus.user.application.internal.dto.UserInternalDto;
import com.lxp.aplus.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public record CourseInstructorDto(
        Long id,
        String nickName
) {
    public static CourseInstructorDto from(UserInternalDto dto) {
        return CourseInstructorDto.builder()
                .id(dto.id())
                .nickName(dto.nickName())
                .build();
    }
}

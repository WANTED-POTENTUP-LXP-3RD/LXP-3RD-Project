package com.lxp.aplus.user.application.internal.dto;

import com.lxp.aplus.user.domain.User;
import lombok.Builder;

@Builder
public record UserInternalDto(
        Long id,
        String nickName,
        String email
) {
    public static UserInternalDto from(User user) {
        return UserInternalDto.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .build();
    }
}

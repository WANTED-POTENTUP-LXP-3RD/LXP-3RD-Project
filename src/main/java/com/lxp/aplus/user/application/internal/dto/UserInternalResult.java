package com.lxp.aplus.user.application.internal.dto;

import com.lxp.aplus.user.domain.User;
import lombok.Builder;

@Builder
public record UserInternalResult(
        Long id,
        String nickName,
        String email
) {
    public static UserInternalResult from(User user) {
        return UserInternalResult.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .build();
    }
}

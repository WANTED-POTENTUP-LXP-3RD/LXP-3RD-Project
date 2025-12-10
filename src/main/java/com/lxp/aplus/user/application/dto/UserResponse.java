package com.lxp.aplus.user.application.dto;

import com.lxp.aplus.user.domain.Role;
import com.lxp.aplus.user.domain.RoleType;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String name,
        String nickName,
        String email,
        String phoneNumber,
        UserStatus status,
        List<RoleType> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getNickName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getStatus(),
                user.getRoles().stream()
                        .filter(role -> role.getDeletedAt() == null) // 삭제되지 않은 Role만 포함
                        .map(Role::getRoleType)
                        .toList(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}


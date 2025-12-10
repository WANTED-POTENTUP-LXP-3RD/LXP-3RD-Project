package com.lxp.aplus.user.application.dto;

import com.lxp.aplus.user.domain.Role;
import com.lxp.aplus.user.domain.RoleType;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserStatus;

import java.util.List;

/**
 * 인증용 사용자 정보 VO
 * 
 * 로그인 등 인증 과정에서 필요한 사용자 정보만 포함합니다.
 * 엔티티를 직접 반환하지 않고 VO로 변환하여 반환합니다.
 */
public record AuthUser(
        Long id,
        String email,
        String nickName,
        String password,
        UserStatus status,
        List<RoleType> roles
) {
    public static AuthUser from(User user) {
        return new AuthUser(
                user.getId(),
                user.getEmail(),
                user.getNickName(),
                user.getPassword(),
                user.getStatus(),
                user.getRoles().stream()
                        .filter(role -> role.getDeletedAt() == null)
                        .map(Role::getRoleType)
                        .toList()
        );
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                ", roles=" + roles +
                '}';
    }
}


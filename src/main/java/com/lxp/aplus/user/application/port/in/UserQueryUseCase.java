package com.lxp.aplus.user.application.port.in;

import com.lxp.aplus.user.application.dto.AuthUser;
import com.lxp.aplus.user.application.dto.response.UserResponse;
import com.lxp.aplus.user.domain.User;

import java.util.Optional;

public interface UserQueryUseCase {
    Optional<User> findById(Long id);

    Optional<User> findByIdWithRoles(Long id);

    Optional<AuthUser> findUserForAuthById(Long id);

    Optional<AuthUser> findUserForAuthByEmail(String email);

    Optional<UserResponse> findUserById(Long id);

    Optional<UserResponse> findUserWithRolesById(Long id);

    boolean existsById(Long id);
}

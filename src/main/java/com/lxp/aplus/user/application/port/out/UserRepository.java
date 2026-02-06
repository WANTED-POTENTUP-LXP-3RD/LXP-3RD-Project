package com.lxp.aplus.user.application.port.out;

import com.lxp.aplus.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findByIdIn(List<Long> ids);
    Optional<User> findUserWithRolesById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findUserWithRolesByEmail(String email);
    boolean existsById(Long id);
}

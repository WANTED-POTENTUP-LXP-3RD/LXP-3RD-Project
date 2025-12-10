package com.lxp.aplus.user.domain;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findUserWithRolesById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findUserWithRolesByEmail(String email);
    boolean existsById(Long id);
}


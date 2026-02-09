package com.lxp.aplus.user.infrastructure.persistence;

import com.lxp.aplus.user.domain.InstructorApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorApplicationJpaRepository extends JpaRepository<InstructorApplication, Long> {
    Optional<InstructorApplication> findByUserId(Long userId);
}

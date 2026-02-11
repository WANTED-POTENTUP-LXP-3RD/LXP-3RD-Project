package com.lxp.aplus.user.infrastructure.persistence;

import com.lxp.aplus.user.domain.InstructorApplication;
import com.lxp.aplus.user.domain.InstructorApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorApplicationJpaRepository extends JpaRepository<InstructorApplication, Long> {
    Optional<InstructorApplication> findByUserId(Long userId);
    Page<InstructorApplication> findAllByStatus(InstructorApplicationStatus status, Pageable pageable);
}

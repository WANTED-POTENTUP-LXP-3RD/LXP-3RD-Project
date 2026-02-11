package com.lxp.aplus.user.application.port.out;

import com.lxp.aplus.user.domain.InstructorApplication;
import com.lxp.aplus.user.domain.InstructorApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InstructorApplicationRepository {
    InstructorApplication save(InstructorApplication application);
    Optional<InstructorApplication> findById(Long id);
    Optional<InstructorApplication> findByUserId(Long userId);
    Page<InstructorApplication> findAll(Pageable pageable);
    Page<InstructorApplication> findAllByStatus(InstructorApplicationStatus status, Pageable pageable);
}

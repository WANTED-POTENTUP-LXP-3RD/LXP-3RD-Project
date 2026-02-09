package com.lxp.aplus.user.application.port.out;

import com.lxp.aplus.user.domain.InstructorApplication;

import java.util.Optional;

public interface InstructorApplicationRepository {
    InstructorApplication save(InstructorApplication application);
    Optional<InstructorApplication> findById(Long id);
    Optional<InstructorApplication> findByUserId(Long userId);
}

package com.lxp.aplus.course.application.port.out;

import com.lxp.aplus.course.presentation.response.InstructorResponse;

import java.util.Optional;

public interface UserQueryPort {
    Optional<InstructorResponse> findInstructorById(Long userId);
}

package com.lxp.aplus.course.application.port.out;

import com.lxp.aplus.course.application.result.InstructorResult;

import java.util.Optional;

public interface UserQueryPort {
    Optional<InstructorResult> findInstructorById(Long userId);
}

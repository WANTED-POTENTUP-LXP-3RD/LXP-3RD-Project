package com.lxp.aplus.user.application.dto.request;

import com.lxp.aplus.user.domain.InstructorApplicationStatus;

public record InstructorApplicationProcessRequest(
        InstructorApplicationStatus status
) {
}

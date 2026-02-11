package com.lxp.aplus.user.application.dto.response;

import com.lxp.aplus.user.domain.InstructorApplication;
import com.lxp.aplus.user.domain.InstructorApplicationStatus;

import java.time.LocalDateTime;

public record InstructorApplicationCreateResponse(
        Long applicationId,
        Long userId,
        InstructorApplicationStatus status,
        LocalDateTime appliedAt
) {
    public static InstructorApplicationCreateResponse from(InstructorApplication application) {
        return new InstructorApplicationCreateResponse(
                application.getId(),
                application.getUserId(),
                application.getStatus(),
                application.getCreatedAt()
        );
    }
}

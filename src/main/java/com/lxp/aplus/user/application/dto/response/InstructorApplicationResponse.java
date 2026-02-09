package com.lxp.aplus.user.application.dto.response;

import com.lxp.aplus.user.domain.InstructorApplication;
import com.lxp.aplus.user.domain.InstructorApplicationStatus;

import java.time.LocalDateTime;

public record InstructorApplicationResponse(
        Long applicationId,
        Long userId,
        InstructorApplicationStatus status,
        LocalDateTime appliedAt
) {
    public static InstructorApplicationResponse from(InstructorApplication application) {
        return new InstructorApplicationResponse(
                application.getId(),
                application.getUserId(),
                application.getStatus(),
                application.getCreatedAt()
        );
    }
}

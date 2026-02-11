package com.lxp.aplus.user.application.dto.response;

import com.lxp.aplus.user.domain.InstructorApplication;
import com.lxp.aplus.user.domain.InstructorApplicationStatus;

import java.time.LocalDateTime;

public record InstructorApplicationResponse(
        Long applicationId,
        Long userId,
        String email,
        String name,
        InstructorApplicationStatus status,
        LocalDateTime appliedAt
) {
    public static InstructorApplicationResponse of(InstructorApplication application, String email, String name) {
        return new InstructorApplicationResponse(
                application.getId(),
                application.getUserId(),
                email,
                name,
                application.getStatus(),
                application.getCreatedAt()
        );
    }
}

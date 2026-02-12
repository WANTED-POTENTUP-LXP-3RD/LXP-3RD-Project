package com.lxp.aplus.progress.infrastructure.adapter.module;

import com.lxp.aplus.enrollment.application.internal.usecase.EnrollmentInternalUseCase;
import com.lxp.aplus.progress.application.port.out.EnrollmentCompletionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentCompletionModuleAdapter implements EnrollmentCompletionPort {

    private final EnrollmentInternalUseCase enrollmentInternalUseCase;

    @Override
    public void completeEnrollment(Long enrollmentId) {
        enrollmentInternalUseCase.completeEnrollment(enrollmentId);
    }
}

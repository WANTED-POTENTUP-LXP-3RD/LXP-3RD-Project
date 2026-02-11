package com.lxp.aplus.enrollment.infrastructure.adapter.module;

import com.lxp.aplus.enrollment.application.port.out.ProgressQueryPort;
import com.lxp.aplus.enrollment.application.port.out.dto.EnrollmentProgressDto;
import com.lxp.aplus.progress.application.internal.usecase.ProgressSummaryInternalUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProgressModuleAdapter implements ProgressQueryPort {

    private final ProgressSummaryInternalUseCase progressSummaryInternalUseCase;

    @Override
    public EnrollmentProgressDto getProgress(Long enrollmentId, Long courseId) {
        return new EnrollmentProgressDto(
                progressSummaryInternalUseCase.getOverallProgressRate(enrollmentId, courseId)
                        .overallProgressRate()
        );
    }

    @Override
    public boolean hasProgress(Long enrollmentId) {
        return progressSummaryInternalUseCase.hasProgress(enrollmentId);
    }

    @Override
    public void removeByEnrollmentId(Long enrollmentId) {
        progressSummaryInternalUseCase.removeByEnrollmentId(enrollmentId);
    }
}

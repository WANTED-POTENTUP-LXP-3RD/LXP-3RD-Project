package com.lxp.aplus.enrollment.adapter.out.external;

import com.lxp.aplus.enrollment.application.port.out.ProgressReader;
import com.lxp.aplus.progress.application.port.in.external.ProgressSummaryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProgressReaderAdapter implements ProgressReader {

    private final ProgressSummaryPort progressSummaryPort;

    @Override
    public int getOverallProgressRate(Long enrollmentId, Long courseId) {
        return progressSummaryPort.getOverallProgressRate(enrollmentId, courseId);
    }

    @Override
    public boolean hasProgress(Long enrollmentId) {
        return progressSummaryPort.hasProgress(enrollmentId);
    }

    @Override
    public void removeByEnrollmentId(Long enrollmentId) {
        progressSummaryPort.removeByEnrollmentId(enrollmentId);
    }
}

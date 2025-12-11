package com.lxp.aplus.enrollment.application.port.out;

import java.util.List;
import java.util.Map;

public interface ProgressFinder {
    Map<Long, Boolean> getCompletionStatusMap(Long enrollmentId, List<Long> lectureResourceIds);
}

package com.lxp.aplus.course.application.port.out;

public interface LectureAnalysisPort {
    void startAnalysisAsync(Long lectureResourceId, String videoUrl, String requestId);
}

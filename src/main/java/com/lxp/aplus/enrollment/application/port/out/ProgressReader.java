package com.lxp.aplus.enrollment.application.port.out;

/**
 * Progress 도메인에서 진도율을 읽어오는 아웃바운드 포트
 */
public interface ProgressReader {
    int getOverallProgressRate(Long enrollmentId, Long courseId);
    boolean hasProgress(Long enrollmentId);
    void removeByEnrollmentId(Long enrollmentId);
}

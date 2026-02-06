package com.lxp.aplus.progress.application.port.in.external;

/**
 * 다른 도메인(Enrollment)에 진도율 정보를 제공하는 Port
 */
public interface ProgressSummaryPort {

    /**
     * 특정 수강의 전체 진도율을 조회한다.
     *
     * @param enrollmentId 수강 ID
     * @param courseId 강좌 ID
     * @return 전체 진도율 (0~100, 반올림된 정수)
     */
    int getOverallProgressRate(Long enrollmentId, Long courseId);

    /**
     * 해당 수강에 대한 학습 기록이 존재하는지 확인한다.
     * @param enrollmentId 수강 ID
     * @return 학습 기록 존재 여부
     */
    boolean hasProgress(Long enrollmentId);

    /**
     * 해당 수강에 대한 모든 학습 기록을 삭제한다. (수강 취소 시 사용)
     * @param enrollmentId 수강 ID
     */
    void removeByEnrollmentId(Long enrollmentId);
}

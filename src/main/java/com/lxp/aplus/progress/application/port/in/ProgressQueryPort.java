package com.lxp.aplus.progress.application.port.in;

import java.util.List;
import java.util.Map;

public interface ProgressQueryPort {
    /**
     * 특정 enrollment에 대한 여러 lectureResource들의 완료 상태를 조회합니다.
     *
     * @param enrollmentId        수강 신청 ID
     * @param lectureResourceIds 조회할 lectureResource ID 목록
     * @return key: lectureResource ID, value: 완료 여부 (true/false)
     */
    Map<Long, Boolean> checkLectureCompletionStatus(Long enrollmentId, List<Long> lectureResourceIds);
}

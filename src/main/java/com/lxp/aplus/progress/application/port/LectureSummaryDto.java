package com.lxp.aplus.progress.application.port;

/**
*  강좌에 포함된 강의 목록(ID, 제목, 재생 시간 포함)을 가져오기 위해 사용
 * 학습 현황을 표시하기 위한 강의 요약 정보를 제공
* */
public record LectureSummaryDto(
    Long resourceId,
    String title,
    int totalDurationSeconds
) {
}

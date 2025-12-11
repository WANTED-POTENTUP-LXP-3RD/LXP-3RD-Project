package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProgressErrorCode implements ErrorCode {
    LEARNING_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "EP001", "학습 이력이 존재하지 않습니다. (수강 신청 확인 필요)"),
    INVALID_PROGRESS_RATE(HttpStatus.BAD_REQUEST, "EP002", "진도율은 0에서 100 사이의 값이어야 합니다."),
    WATCHED_DURATION_EXCEEDS_TOTAL(HttpStatus.BAD_REQUEST, "EP003", "시청 시간이 전체 영상 길이를 초과할 수 없습니다."),
    CANNOT_UPDATE_EXPIRED_ENROLLMENT(HttpStatus.CONFLICT, "EP004", "수강 기간이 만료되어 진도율을 갱신할 수 없습니다."),
    CANNOT_UPDATE_PROGRESS_FOR_NON_ENROLLED(HttpStatus.BAD_REQUEST, "EP005", "수강 중인 강의만 진도율을 업데이트할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

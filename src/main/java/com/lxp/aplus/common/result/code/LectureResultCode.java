package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LectureResultCode implements ResultCode {
    LECTURE_REGISTER_SUCCESS(HttpStatus.CREATED, "SL001", "강의가 정상적으로 등록되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

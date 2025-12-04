package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CourseResultCode implements ResultCode {
    COURSE_REGISTER_SUCCESS(HttpStatus.CREATED, "SC001", "강의가 정상적으로 등록되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

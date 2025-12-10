package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProgressResultCode implements ResultCode {
    UPDATE_PROGRESS_SUCCESS(HttpStatus.OK, "SP001", "진도율이 갱신되었습니다."),
    GET_PROGRESS_SUCCESS(HttpStatus.OK, "SP002", "학습 이력을 조회하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

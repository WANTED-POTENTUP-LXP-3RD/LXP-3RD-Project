package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LectureErrorCode implements ErrorCode {
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "EL001", "해당 강의를 찾을 수 없습니다."),
    LECTURE_CREATE_FAILED(HttpStatus.BAD_REQUEST, "EL002", "강의 생성에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LectureResultCode implements ResultCode {
    LECTURE_REGISTER_SUCCESS(HttpStatus.CREATED, "SL001", "강의가 정상적으로 등록되었습니다."),
    LECTURE_UPDATE_SUCCESS(HttpStatus.OK, "SL002", "강의 정보가 수정되었습니다."),
    LECTURE_READ_SUCCESS(HttpStatus.OK, "SL003", "강의 정보에 성공했습니다."),
    LECTURE_DELETE_SUCCESS(HttpStatus.OK, "SL004", "강의가 정상적으로 삭제되었습니다."),
    LECTURE_RESOURCE_REGISTER_SUCCESS(HttpStatus.CREATED, "SL005", "강의 리소스가 정상적으로 등록되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

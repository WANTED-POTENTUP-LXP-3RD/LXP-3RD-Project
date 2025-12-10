package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CourseResultCode implements ResultCode {
    COURSE_REGISTER_SUCCESS(HttpStatus.CREATED, "SC001", "강좌가 정상적으로 등록되었습니다."),
    COURSE_UPDATE_SUCCESS(HttpStatus.OK, "SC002", "강좌 정보가 수정되었습니다."),
    COURSE_READ_SUCCESS(HttpStatus.OK, "SC003","강좌 조회에 성공했습니다."),
    COURSE_LIST_SUCCESS(HttpStatus.OK, "SC004", "강좌 목록 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

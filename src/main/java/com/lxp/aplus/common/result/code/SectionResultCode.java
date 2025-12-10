package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SectionResultCode implements ResultCode {
    SECTION_REGISTER_SUCCESS(HttpStatus.CREATED, "SS001", "섹션이 정상적으로 등록되었습니다."),
    SECTION_UPDATE_SUCCESS(HttpStatus.OK, "SS002", "섹션 정보가 수정되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

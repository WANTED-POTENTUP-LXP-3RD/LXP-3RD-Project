package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ErrorCode {
    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EM001", "메일 발송에 실패했습니다."),
    MAIL_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "EM002", "메일 템플릿을 찾을 수 없습니다."),
    INVALID_MAIL_RECIPIENT(HttpStatus.BAD_REQUEST, "EM003", "유효하지 않은 수신자입니다."),
    INVALID_MAIL_SENDER(HttpStatus.BAD_REQUEST, "EM004", "유효하지 않은 발신자입니다."),
    MISSING_TEMPLATE_VARIABLE(HttpStatus.BAD_REQUEST, "EM005", "메일 템플릿에서 요구하는 변수가 존재하지 않습니다."),
    INVALID_TEMPLATE_VARIABLE_TYPE(HttpStatus.BAD_REQUEST, "EM006", "템플릿 코드에 맞지 않는 변수 타입입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}


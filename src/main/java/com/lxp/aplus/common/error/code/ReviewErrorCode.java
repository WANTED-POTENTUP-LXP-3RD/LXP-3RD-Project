package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    INVALID_RATING(HttpStatus.BAD_REQUEST, "ER001", "유효하지 않은 범위의 평점입니다."),
    CANT_REVIEW_IN_OWN_COURSE(HttpStatus.BAD_REQUEST,"ER002","스스로의 강좌에는 리뷰를 작성할 수 없습니다."),
    CANT_REVIEW_IN_NOT_ENROLLED(HttpStatus.BAD_REQUEST,"ER003","수강하지 않은 강좌에는 리뷰를 작성할 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

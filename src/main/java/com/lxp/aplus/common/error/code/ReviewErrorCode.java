package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    INVALID_RATING_RANGE(HttpStatus.BAD_REQUEST, "ER001", "유효하지 않은 범위의 평점입니다."),
    CANT_REVIEW_IN_OWN_COURSE(HttpStatus.BAD_REQUEST,"ER002","스스로의 강좌에는 리뷰를 작성할 수 없습니다."),
    CANT_REVIEW_IN_NOT_ENROLLED(HttpStatus.BAD_REQUEST,"ER003","수강하지 않은 강좌에는 리뷰를 작성할 수 없습니다."),
    ALREADY_REGISTER_IN_COURSE(HttpStatus.BAD_REQUEST,"ER004","이미 리뷰를 작성한 강좌입니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"ER005","해당 리뷰를 찾을 수 없습니다."),
    NOT_OWN_REVIEW(HttpStatus.FORBIDDEN,"ER006","해당 리뷰의 작성자가 아닙니다."),
    COURSE_IS_NOT_PUBLISHED(HttpStatus.BAD_REQUEST,"ER007","해당 강좌는 발행되지 않았습니다."),
    AI_ANALYZE_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "ER020", "리뷰 분석에 실패했습니다."),
    AI_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "ER022", "AI 서버에 연결할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

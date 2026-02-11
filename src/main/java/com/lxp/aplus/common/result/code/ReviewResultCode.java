package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewResultCode implements ResultCode {
    REVIEW_CREATE_SUCCESS(HttpStatus.CREATED,"SR001", "리뷰가 성공적으로 등록되었습니다."),
    REVIEW_UPDATE_SUCCESS(HttpStatus.OK,"SR002", "리뷰가 수정되었습니다."),
    REVIEW_DELETE_SUCCESS(HttpStatus.OK,"SR003", "리뷰가 삭제되었습니다."),
    REVIEW_FIND_SUCCESS(HttpStatus.OK,"SR004", "리뷰 조회에 성공하였습니다."),
    REVIEW_ANALYZE_SUCCESS(HttpStatus.OK, "SR005", "리뷰 분석이 성공하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
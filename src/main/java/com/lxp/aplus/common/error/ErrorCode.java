package com.lxp.aplus.common.error;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드에 대한 Enum class
 * 에러 코드 형식
 * 1. 모든 에러 코드는 "E"로 시작한다.
 * 2. 2번째 문자는 에러가 발생한 카테고리를 나타낸다.
 * ex) "U": User, "A": Article, "C": Collection, "F": File
 */
public interface ErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}

package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserResultCode implements ResultCode {
    USER_CREATE_SUCCESS(HttpStatus.CREATED, "SU001", "사용자가 정상적으로 생성되었습니다."),
    USER_UPDATE_SUCCESS(HttpStatus.OK, "SU002", "사용자 정보가 수정되었습니다."),
    USER_ROLE_ADD_SUCCESS(HttpStatus.OK, "SU003", "사용자 역할이 추가되었습니다."),
    USER_WITHDRAW_SUCCESS(HttpStatus.OK, "SU004", "사용자가 탈퇴 처리되었습니다."),
    USER_ACTIVATE_SUCCESS(HttpStatus.OK, "SU005", "사용자가 활성화되었습니다."),
    USER_PASSWORD_CHANGE_SUCCESS(HttpStatus.OK, "SU006", "비밀번호가 변경되었습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "SU007", "사용자가 삭제되었습니다."),
    USER_GET_SUCCESS(HttpStatus.OK, "SU008", "사용자 정보 조회에 성공하였습니다."),
    USER_LIST_GET_SUCCESS(HttpStatus.OK, "SU009", "사용자 목록 조회에 성공하였습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "SU010", "로그인에 성공하였습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "SU011", "로그아웃에 성공하였습니다."),
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "SU012", "토큰이 재발급되었습니다."),
    INSTRUCTOR_APPLICATION_SUCCESS(HttpStatus.OK, "SU013", "강사 권한을 요청했습니다."),
    INSTRUCTOR_APPLICATION_APPROVED(HttpStatus.OK, "SU015", "강사 권한이 수락되었습니다."),
    INSTRUCTOR_APPLICATION_REJECTED(HttpStatus.OK, "SU016", "강사 권한이 거절되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

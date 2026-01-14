package com.lxp.aplus.common.result.code;

import com.lxp.aplus.common.result.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EnrollmentResultCode implements ResultCode {
    ENROLLMENT_SUCCESS(HttpStatus.CREATED, "SE001", "결제 검증 및 수강 신청이 정상적으로 완료되었습니다."),
    GET_ENROLLMENTS_SUCCESS(HttpStatus.OK, "SE002", "수강 목록 조회에 성공하였습니다."),
    GET_ENROLLMENT_DETAIL_SUCCESS(HttpStatus.OK, "SE003", "수강 상세 정보 조회에 성공하였습니다."),
    CANCEL_ENROLLMENT_SUCCESS(HttpStatus.OK, "SE004", "수강이 정상적으로 취소되었습니다."),
    CANCEL_ENROLLMENT_REQUEST_ACCEPTED(HttpStatus.ACCEPTED, "SE005", "수강 취소 요청이 정상적으로 접수되었습니다. 환불 처리에는 시간이 소요될 수 있습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}

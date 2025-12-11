package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EnrollmentErrorCode implements ErrorCode {
    ALREADY_ENROLLED_COURSE(HttpStatus.CONFLICT, "EE001", "이미 수강 중인 강의가 포함되어 있습니다."),
    ENROLLMENT_COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "EE002", "존재하지 않는 강의가 포함되어 있습니다."),
    INVALID_ENROLLMENT_STATUS(HttpStatus.BAD_REQUEST, "EE003", "지원하지 않는 수강 상태(status) 값입니다."),
    ENROLLMENT_NOT_FOUND_OR_NO_ACCESS(HttpStatus.NOT_FOUND, "EE004", "해당 수강 내역을 찾을 수 없거나 접근 권한이 없습니다."),
    ENROLLMENT_TO_CANCEL_NOT_FOUND(HttpStatus.NOT_FOUND, "EE005", "취소할 수강 내역이 존재하지 않습니다."),
    CANNOT_CANCEL_COMPLETED_ENROLLMENT(HttpStatus.CONFLICT, "EE006", "이미 수강이 완료(수료)되어 취소할 수 없습니다."),
    CANNOT_CANCEL_EXPIRED_ENROLLMENT(HttpStatus.CONFLICT, "EE007", "수강 기간이 만료되어 취소할 수 없습니다."),
    ALREADY_CANCELLED_ENROLLMENT(HttpStatus.CONFLICT, "EE008", "이미 취소 처리된 건입니다."),
    ENROLLMENT_EXPIRED_HISTORY_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "EE009", "수강 기간이 만료되어 학습 이력을 조회할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

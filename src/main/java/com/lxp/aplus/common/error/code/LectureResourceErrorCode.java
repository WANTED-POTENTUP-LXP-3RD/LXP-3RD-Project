package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LectureResourceErrorCode implements ErrorCode {
    LECTURE_RESOURCE_FILE_REQUIRED(HttpStatus.BAD_REQUEST, "ER001", "강의자료 파일은 필수입니다."),
    LECTURE_RESOURCE_UNSUPPORTED_EXTENSION(HttpStatus.BAD_REQUEST, "ER002", "지원하지 않는 파일 확장자 입니다."),
    LECTURE_RESOURCE_FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "ER003", "파일 크기 제한 초과입니다."),
    LECTURE_RESOURCE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ER004", "파일 업로드 중 내부 오류가 발생했습니다."),
    LECTURE_RESOURCE_NOT_EXIST(HttpStatus.NOT_FOUND, "ER005", "존재하지 않는 리소스 입니다."),
    STORAGE_CLIENT_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ER006", "스토리지 서버 오류입니다."),
    LECTURE_RESOURCE_VIDEO_DURATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ER007", "동영상 리소스는 duration 값이 필수 입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

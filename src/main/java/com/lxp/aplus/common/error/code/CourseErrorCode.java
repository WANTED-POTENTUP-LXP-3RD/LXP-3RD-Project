package com.lxp.aplus.common.error.code;

import com.lxp.aplus.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CourseErrorCode implements ErrorCode {
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "EC001", "해당 강좌를 찾을 수 없습니다."),
    COURSE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "EC002", "해당 강좌에 대한 접근 권한이 없습니다."),
    CANNOT_MODIFY_PUBLISHED_COURSE(HttpStatus.BAD_REQUEST, "EC003", "이미 발행된 강좌는 수정하거나 삭제할 수 없습니다."),
    COURSE_SECTION_EMPTY(HttpStatus.BAD_REQUEST, "EC004", "강좌에는 최소 1개 이상의 섹션이 있어야 합니다."),
    COURSE_LECTURE_EMPTY(HttpStatus.BAD_REQUEST, "EC005", "모든 섹션에는 최소 1개 이상의 강의가 있어야 합니다."),
    COURSE_CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "EC006", "모든 강의에는 영상 또는 자료가 등록되어야 합니다."),
    COURSE_THUMBNAIL_EXTENSION_MISSING(HttpStatus.BAD_REQUEST, "EC007", "강좌 썸네일 파일에는 확장자가 포함되어야 합니다."),
    COURSE_THUMBNAIL_EXTENSION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "EC008", "강좌 썸네일은 jpg, jpeg, png, webp 형식만 업로드할 수 있습니다."),
    COURSE_THUMBNAIL_CONTENT_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "EC009", "강좌 썸네일은 image/jpeg, image/png, image/webp 형식만 업로드할 수 있습니다."),;




    private final HttpStatus status;
    private final String code;
    private final String message;
}

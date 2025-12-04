package com.lxp.aplus.common.result;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ResultResponse<T>(
        HttpStatus status,
        String code,
        String message,
        T data
) {
    public static <T> ResultResponse<T> of(ResultCode resultCode, T data) {
        return ResultResponse.<T>builder()
                .status(resultCode.getStatus())
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> ResultResponse<T> from(ResultCode resultCode) {
        return ResultResponse.<T>builder()
                .status(resultCode.getStatus())
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .data(null)
                .build();
    }
}

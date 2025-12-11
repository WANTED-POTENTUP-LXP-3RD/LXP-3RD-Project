package com.lxp.aplus.course.application.vo;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Builder
public record UploadFile(
        String originalFileName,
        long size,
        String contentType,
        InputStream inputStream
) {
    public static UploadFile from(MultipartFile file) throws IOException {
        return UploadFile.builder()
                .originalFileName(file.getOriginalFilename())
                .size(file.getSize())
                .contentType(file.getContentType())
                .inputStream(file.getInputStream())
                .build();
    }
}

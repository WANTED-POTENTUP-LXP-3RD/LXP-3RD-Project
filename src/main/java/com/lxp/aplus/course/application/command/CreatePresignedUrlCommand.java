package com.lxp.aplus.course.application.command;

import lombok.Builder;

@Builder
public record CreatePresignedUrlCommand(
        String fileName,
        String contentType,
        Long size,
        Integer duration,
        Boolean isDownloadable
) {
}

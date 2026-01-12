package com.lxp.aplus.course.application.command;

public record CreatePresignedUrlCommand(
        String fileName,
        String contentType,
        Integer size,
        Integer duration,
        Boolean isDownloadable
) {
}

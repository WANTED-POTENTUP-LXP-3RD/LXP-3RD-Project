package com.lxp.aplus.course.infrastructure.file;

public record StoredFileInfo(
        String fileUrl,
        String fileKey
) {
    public static StoredFileInfo of(String fileKey, String fileUrl) {
        return new StoredFileInfo(fileKey, fileUrl);
    }
}

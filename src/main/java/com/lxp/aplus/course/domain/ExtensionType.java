package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ExtensionType {
    MP4("mp4", ResourceType.VIDEO),
    PDF("pdf", ResourceType.PDF),
    DOC("doc", ResourceType.DOC),
    ZIP("zip", ResourceType.ZIP);

    private final String extension;
    private final ResourceType resourceType;

    public static ExtensionType fromFileName(String fileName) {
        String ext = extractExtension(fileName);

        return Arrays.stream(values())
                .filter(e -> e.extension.equalsIgnoreCase(ext))
                .findFirst()
                .orElseThrow(() -> new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_UNSUPPORTED_EXTENSION));
    }

    public static String extractExtension(String fileName) {
        int idx = fileName.lastIndexOf(".");
        if (idx == -1 || idx == fileName.length() -1) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_UNSUPPORTED_EXTENSION);
        }
        return fileName.substring(idx + 1);
    }
}

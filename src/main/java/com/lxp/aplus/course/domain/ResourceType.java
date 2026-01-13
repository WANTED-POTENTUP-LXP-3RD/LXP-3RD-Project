package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum ResourceType {
    VIDEO("영상", List.of("mp4"), 1000000L),
    PDF("PDF 자료", List.of("pdf"), 50000L),
    DOC("문서", List.of("doc"), 50000L),
    ZIP("압축 파일", List.of("zip"), 1000000L);

    @Getter
    private final String displayName;

    private final List<String> extensions;

    private final Long maxSize;

    public static ResourceType fromExtension(String extension) {
        String ext = extension.toLowerCase();
        return Arrays.stream(values())
                .filter((type -> type.extensions.contains(ext)))
                .findFirst()
                .orElseThrow(() -> new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_UNSUPPORTED_EXTENSION));

    }

    public void validateFileSize(long fileSize) {
        if (fileSize > maxSize) {
            throw new BusinessException(LectureResourceErrorCode.FILE_SIZE_EXCEEDED);
        }
    }
}

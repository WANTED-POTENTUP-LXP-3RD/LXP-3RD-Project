package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum ResourceType {
    VIDEO("영상", List.of("mp4")),
    PDF("PDF 자료", List.of("pdf")),
    DOC("문서", List.of("doc")),
    ZIP("압축 파일", List.of("zip"));

    @Getter
    private final String displayName;

    private final List<String> extensions;

    public static ResourceType fromExtension(String extension) {
        String ext = extension.toLowerCase();
        return Arrays.stream(values())
                .filter((type -> type.extensions.contains(ext)))
                .findFirst()
                .orElseThrow(() -> new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_UNSUPPORTED_EXTENSION));

    }
}

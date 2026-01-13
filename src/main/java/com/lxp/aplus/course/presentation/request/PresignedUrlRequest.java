package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.CreatePresignedUrlCommand;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record PresignedUrlRequest(
        @NotBlank(message = "파일명은 필수입니다")
        String fileName,
        @NotBlank(message = "콘텐츠 타입은 필수입니다")
        String contentType,
        @NotNull(message = "파일 크기는 필수입니다")
        @Min(value = 1, message = "파일 크기는 0 이상이어야 합니다")
        Long size,
        Integer duration,
        @NotNull(message = "다운로드 가능 여부는 필수입니다")
        Boolean isDownloadable
) {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "video/mp4", "application/pdf", "application/zip", "application/msword"
    );

    @AssertTrue(message = "콘텐츠 타입은 video/mp4, application/pdf, application/zip, application/msword만 허용됩니다")
    public boolean isValidContentType() {
        if (contentType == null || contentType.isBlank()) {
            return true;
        }
        return ALLOWED_CONTENT_TYPES.contains(contentType);
    }

    public CreatePresignedUrlCommand toCommand() {
        return CreatePresignedUrlCommand.builder()
                .fileName(this.fileName())
                .contentType(this.contentType())
                .size(this.size())
                .duration(this.duration())
                .isDownloadable(this.isDownloadable())
                .build();
    }
}

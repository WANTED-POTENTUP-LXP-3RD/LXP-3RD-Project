package com.lxp.aplus.course.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseThumbnailPresignCommand(

        @NotBlank(message = "파일명은 필수입니다.")
        @Size(max = 255, message = "파일명은 255자를 넘어갈 수 없습니다.")
        String originalFileName,

        @NotBlank(message = "컨텐츠 타입은 필수입니다.")
        String contentType
) {}

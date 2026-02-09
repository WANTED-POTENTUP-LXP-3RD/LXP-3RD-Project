package com.lxp.aplus.course.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseThumbnailPresignCommand(

        @NotBlank
        @Size(max = 255)
        String originalFileName,

        @NotBlank
        String contentType
) {}

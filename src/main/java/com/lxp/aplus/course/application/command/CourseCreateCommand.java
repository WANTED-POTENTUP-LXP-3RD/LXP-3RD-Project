package com.lxp.aplus.course.application.command;

import com.lxp.aplus.course.domain.CourseLevel;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record CourseCreateCommand(
        Long instructorId,
        String title,
        String summary,
        String description,
        Long categoryId,
        int price,
        String thumbnailUrl,
        MultipartFile thumbnailFile,
        CourseLevel courseLevel,
        String thumbnailResourceKey
) {
}

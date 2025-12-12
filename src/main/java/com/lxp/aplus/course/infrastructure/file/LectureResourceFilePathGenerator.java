package com.lxp.aplus.course.infrastructure.file;

import com.lxp.aplus.course.application.port.out.FilePathGenerator;
import com.lxp.aplus.course.domain.ExtensionType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LectureResourceFilePathGenerator implements FilePathGenerator {

    private static final String DIRECTORY_FORMAT =
            "resources/lectureResource/%d/%s.%s";

    @Override
    public String generate(Long courseId, String originalFileName) {
        String extension = ExtensionType.extractExtension(originalFileName);

        String uuid = UUID.randomUUID().toString();

        return String.format(
                DIRECTORY_FORMAT,
                courseId,
                uuid,
                extension.toLowerCase()
        );
    }
}

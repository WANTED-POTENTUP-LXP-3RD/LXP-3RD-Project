package com.lxp.aplus.course.infrastructure.file.minio;

import com.lxp.aplus.course.domain.ExtensionType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LectureResourceKeyGenerator {
    public String generate(String bucket, String originalFileName) {
        String extension = ExtensionType.extractExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return bucket + "/" + uuid + "." + extension;
    }
}

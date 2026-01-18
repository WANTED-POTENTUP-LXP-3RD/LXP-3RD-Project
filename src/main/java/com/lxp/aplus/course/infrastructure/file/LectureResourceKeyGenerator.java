package com.lxp.aplus.course.infrastructure.file;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LectureResourceKeyGenerator {
    public String generate(String bucket, String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        return bucket + "/" + uuid;
    }
}

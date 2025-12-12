package com.lxp.aplus.course.infrastructure.file;

import com.lxp.aplus.course.application.port.out.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@Component
public class LocalFileStorage implements FileStorage {

    private final Path basePath;
    private final String publicUrlPrefix;

    public LocalFileStorage(
            @Value("${file.storage.base-path}") String basePath,
            @Value("${file.storage.public-url-prefix}") String publicUrlPrefix
    ) {
        this.basePath = Paths.get(basePath);
        this.publicUrlPrefix = publicUrlPrefix;
    }

    @Override
    public void save(String fileKey, InputStream inputStream, long size, String contentType) {
        Path target = basePath.resolve(fileKey).normalize();

        try {
            // 디렉토리 없으면 생성
            Files.createDirectories(target.getParent());

            try (OutputStream os = Files.newOutputStream(
                    target,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                inputStream.transferTo(os);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public void delete(String fileKey) {
        Path target = basePath.resolve(fileKey).normalize();

        try {
            Files.deleteIfExists(target);
            log.info("File deleted. key={}", fileKey);
        } catch (IOException e) {
            log.error("Failed to delete file. key={}", fileKey, e);
        }
    }
}

package com.lxp.aplus.course.infrastructure.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUrlGenerator {

    @Value("${app.cdn.base-url}")
    private String baseUrl;

    public String generate(String fileKey) {
        return baseUrl + "/" + fileKey;
    }
}

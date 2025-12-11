package com.lxp.aplus.common.file;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class LocalFileUploader implements FileUploader {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT);
        }

        try {
            // 디렉토리 생성
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일명 중복 방지
            String originalFilename = file.getOriginalFilename();
            String storeFileName = UUID.randomUUID() + "_" + originalFilename;
            String filePath = UPLOAD_DIR + storeFileName;

            // 파일 저장
            file.transferTo(new File(filePath));

            // 저장된 파일 경로 또는 접근 가능한 URL 반환
            // 예시로 로컬 경로를 반환하지만, 실제로는 CDN/S3 URL이어야 합니다.
            return "/uploads/" + storeFileName; 

        } catch (IOException e) {
            throw new BusinessException(GlobalErrorCode.INTERNAL_ERROR);
        }
    }
}

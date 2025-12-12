package com.lxp.aplus.course.application.port.out;

import java.io.InputStream;

public interface FileStorage {
    /**
     * 파일 저장
     * @param fileKey       저장 경로
     * @param inputStream   파일 내용 스트림
     * @param size          파일 크기 (bytes)
     * @param contentType   MIME 타입 (e.g. video/mp4, application/pdf)
     */
    void save(String fileKey, InputStream inputStream, long size, String contentType);

    /**
     * 파일 삭제
     */
    void delete(String fileKey);
}


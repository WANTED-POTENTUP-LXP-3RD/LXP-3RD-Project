package com.lxp.aplus.course.application.port.out;

import com.lxp.aplus.course.application.result.PresignedUrlResult;

public interface PresignedUrlGenerator {
    PresignedUrlResult generateGetUrl(String key);

    PresignedUrlResult generatePutUrl(String originalFileName, String contentType);

    PresignedUrlResult generateDeleteUrl(String key);

    PresignedUrlResult generatePutUrlWithKey(String key, String contentType);

}

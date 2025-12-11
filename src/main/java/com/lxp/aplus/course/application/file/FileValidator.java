package com.lxp.aplus.course.application.file;

import com.lxp.aplus.course.application.vo.UploadFile;

public interface FileValidator {
    void validateRequired(UploadFile file);
    void validateIfPresent(UploadFile file);
}

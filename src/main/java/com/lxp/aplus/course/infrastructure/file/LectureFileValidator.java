package com.lxp.aplus.course.infrastructure.file;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import com.lxp.aplus.course.application.file.FileValidator;
import com.lxp.aplus.course.application.vo.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureFileValidator implements FileValidator {

    private final long MAX_FILE_SIZE = 480L * 1024 * 1024; // 480MB

    @Override
    public void validateRequired(UploadFile file) {
        if (file == null || file.size() <= 0) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_FILE_REQUIRED);
        }

        if (file.size() > MAX_FILE_SIZE) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_FILE_SIZE_EXCEEDED);
        }
    }

    @Override
    public void validateIfPresent(UploadFile file) {
        if (file == null) {
            return;
        }
        if (file.size() > MAX_FILE_SIZE) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_FILE_SIZE_EXCEEDED);
        }
    }
}

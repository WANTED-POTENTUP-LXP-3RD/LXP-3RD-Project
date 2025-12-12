package com.lxp.aplus.course.infrastructure.file;

import com.lxp.aplus.course.application.port.out.FilePathGenerator;
import com.lxp.aplus.course.application.port.out.FileStorage;
import com.lxp.aplus.course.application.vo.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureFileStore {

    private final FilePathGenerator filePathGenerator;
    private final FileUrlGenerator fileUrlGenerator;
    private final FileStorage fileStorage;

    public StoredFileInfo storeLectureResourceFile(Long courseId, UploadFile file) {
        String fileKey = filePathGenerator.generate(
                courseId,
                file.originalFileName()
        );

        String fileUrl = fileUrlGenerator.generate(fileKey);

        fileStorage.save(
                fileKey,
                file.inputStream(),
                file.size(),
                file.contentType()
        );

        return StoredFileInfo.of(fileKey, fileUrl);
    }
}

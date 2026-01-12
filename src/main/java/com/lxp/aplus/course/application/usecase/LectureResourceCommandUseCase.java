package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.course.application.command.CreatePresignedUrlCommand;
import com.lxp.aplus.course.application.port.out.PresignedUrlGenerator;
import com.lxp.aplus.course.application.result.PresignedUrlResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureResourceCommandUseCase {
    private final PresignedUrlGenerator presignedUrlGenerator;

    public PresignedUrlResult generatePresignedUrl(CreatePresignedUrlCommand command) {
        // TODO: LectureResource에 메타 데이터 저장
        return presignedUrlGenerator.generatePutUrl(command.fileName(), command.contentType());
    }

    public PresignedUrlResult generateGetUrl(String key) {
        return presignedUrlGenerator.generateGetUrl(key);
    }
}

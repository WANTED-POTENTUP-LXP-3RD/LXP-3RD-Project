package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.course.application.command.CreatePresignedUrlCommand;
import com.lxp.aplus.course.application.port.out.PresignedUrlGenerator;
import com.lxp.aplus.course.application.result.PresignedUrlResult;
import com.lxp.aplus.course.domain.LectureResourceRepository;
import com.lxp.aplus.course.domain.LectureResourceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureResourceCommandUseCase {
    private final PresignedUrlGenerator presignedUrlGenerator;
    private final LectureResourceRepository lectureResourceRepository;

    public PresignedUrlResult generatePresignedUrl(CreatePresignedUrlCommand command) {
        PresignedUrlResult presignedUrlResult = presignedUrlGenerator.generatePutUrl(
                command.fileName(),
                command.contentType()
        );

        LectureResourceV2 lectureResource = LectureResourceV2.create(
                command.fileName(),
                presignedUrlResult.key(),
                command.duration(),
                command.isDownloadable()
        );

        lectureResourceRepository.save(lectureResource);
        return presignedUrlResult;
    }

    public PresignedUrlResult generateGetUrl(String key) {
        return presignedUrlGenerator.generateGetUrl(key);
    }
}

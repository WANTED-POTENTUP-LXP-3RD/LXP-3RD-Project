package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.application.command.CourseThumbnailPresignCommand;
import com.lxp.aplus.course.application.port.out.PresignedUrlGenerator;
import com.lxp.aplus.course.application.result.CourseThumbnailPresignResult;
import com.lxp.aplus.course.application.result.PresignedUrlResult;
import com.lxp.aplus.course.infrastructure.file.CourseThumbnailKeyGenerator;
import com.lxp.aplus.course.infrastructure.file.FileUrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseThumbnailPresignUseCase {

    private static final Set<String> ALLOWED = Set.of("image/jpeg","image/png","image/webp");

    private final PresignedUrlGenerator presignedUrlGenerator;
    private final CourseThumbnailKeyGenerator keyGenerator;
    private final FileUrlGenerator fileUrlGenerator;

    public CourseThumbnailPresignResult presign(Long instructorId, CourseThumbnailPresignCommand req) {
        if (!ALLOWED.contains(req.contentType())) {
            throw new BusinessException(CourseErrorCode.COURSE_THUMBNAIL_CONTENT_TYPE_NOT_ALLOWED);
        }

        String key = keyGenerator.generate(instructorId, req.originalFileName());

        PresignedUrlResult presigned = presignedUrlGenerator.generatePutUrlWithKey(key, req.contentType());

        return CourseThumbnailPresignResult.of(
                presigned.url(),
                presigned.key(),
                fileUrlGenerator.generate(presigned.key()),
                presigned.expireSeconds()
        );

    }
}

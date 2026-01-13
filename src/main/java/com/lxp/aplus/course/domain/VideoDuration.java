package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import jakarta.persistence.Embeddable;

@Embeddable
public record VideoDuration(
        Integer duration
) {
    public VideoDuration {
        if (duration == null || duration <= 0) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_VIDEO_DURATION_NOT_FOUND);
        }
    }

    public static VideoDuration from(Integer duration) {
        return new VideoDuration(duration);
    }
}

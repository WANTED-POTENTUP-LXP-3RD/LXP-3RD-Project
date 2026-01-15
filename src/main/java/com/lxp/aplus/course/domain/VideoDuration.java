package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoDuration {

    @Column(name = "duration")
    private Integer duration;

    private VideoDuration(Integer duration) {
        validate(duration);
        this.duration = duration;
    }

    public static VideoDuration from(Integer duration) {
        return new VideoDuration(duration);
    }

    private void validate(Integer duration) {
        if (duration == null || duration <= 0) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_VIDEO_DURATION_NOT_FOUND);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoDuration that = (VideoDuration) o;
        return Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration);
    }
}
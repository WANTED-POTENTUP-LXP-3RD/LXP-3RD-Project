package com.lxp.aplus.progress.application.port.out;

import com.lxp.aplus.progress.application.port.out.dto.LectureDurationDto;
import com.lxp.aplus.progress.application.port.out.dto.LectureSummaryDto;

import java.util.List;

public interface LectureProvider {
    LectureDurationDto getLectureInfo(Long lectureResourceId);

    List<LectureSummaryDto> getLectureDetailsByCourseId(Long courseId);
}

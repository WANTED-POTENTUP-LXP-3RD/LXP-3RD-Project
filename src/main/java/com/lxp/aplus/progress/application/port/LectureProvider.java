package com.lxp.aplus.progress.application.port;

import java.util.List;

public interface LectureProvider {
    LectureDurationDto getLectureInfo(Long lectureResourceId);
    List<LectureSummaryDto> getLectureDetailsByCourseId(Long courseId);
}
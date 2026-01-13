package com.lxp.aplus.progress.application.port;

import java.util.List;

public interface LectureProvider {
    LectureInfo getLectureInfo(Long lectureResourceId);
    List<LectureDetailInfo> getLectureDetailsByCourseId(Long courseId);
}
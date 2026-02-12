package com.lxp.aplus.course.domain;

import java.util.List;

public interface LectureKeywordRepository {
    void deleteByLectureResourceId(Long lectureResourceId);

    List<LectureKeyword> saveAll(List<LectureKeyword> keywords);
}

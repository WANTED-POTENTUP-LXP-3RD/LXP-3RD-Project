package com.lxp.aplus.course.domain;

import java.util.Optional;

public interface LectureResourceRepository {
    LectureResourceV2 save(LectureResourceV2 lectureResource);
    Optional<LectureResourceV2> findByKey(String key);
}

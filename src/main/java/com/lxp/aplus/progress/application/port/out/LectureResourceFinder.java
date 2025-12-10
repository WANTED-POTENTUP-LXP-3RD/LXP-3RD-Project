package com.lxp.aplus.progress.application.port.out;

import com.lxp.aplus.course.domain.LectureResource;
import java.util.Optional;

public interface LectureResourceFinder {
    Optional<LectureResource> findLectureResourceById(Long resourceId);
}

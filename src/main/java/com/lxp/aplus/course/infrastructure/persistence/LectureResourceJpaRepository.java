package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.LectureResourceV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectureResourceJpaRepository extends JpaRepository<LectureResourceV2, Long> {
    Optional<LectureResourceV2> findByFileKey(String fileKey);
}

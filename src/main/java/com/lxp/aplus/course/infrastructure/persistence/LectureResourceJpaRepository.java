package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.LectureResourceV2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureResourceJpaRepository extends JpaRepository<LectureResourceV2, Long> {
}

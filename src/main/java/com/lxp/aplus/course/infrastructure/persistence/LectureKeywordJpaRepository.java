package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.LectureKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureKeywordJpaRepository extends JpaRepository<LectureKeyword, Long> {
    void deleteByLectureResourceId(Long lectureResourceId);

    List<LectureKeyword> findByLectureResourceId(Long lectureResourceId);
}

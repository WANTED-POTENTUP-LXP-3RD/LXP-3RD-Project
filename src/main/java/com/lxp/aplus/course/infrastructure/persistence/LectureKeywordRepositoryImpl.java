package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.LectureKeyword;
import com.lxp.aplus.course.domain.LectureKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureKeywordRepositoryImpl implements LectureKeywordRepository {
    private final LectureKeywordJpaRepository lectureKeywordJpaRepository;

    @Override
    public void deleteByLectureResourceId(Long lectureResourceId) {
        lectureKeywordJpaRepository.deleteByLectureResourceId(lectureResourceId);
    }

    @Override
    public List<LectureKeyword> saveAll(List<LectureKeyword> keywords) {
        return lectureKeywordJpaRepository.saveAll(keywords);
    }
}

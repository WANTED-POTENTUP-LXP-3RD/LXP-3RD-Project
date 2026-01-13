package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.LectureResourceRepository;
import com.lxp.aplus.course.domain.LectureResourceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LectureResourceRepositoryImpl implements LectureResourceRepository {
    private final LectureResourceJpaRepository lectureResourceJpaRepository;

    @Override
    public LectureResourceV2 save(LectureResourceV2 lectureResource) {
        return lectureResourceJpaRepository.save(lectureResource);
    }
}

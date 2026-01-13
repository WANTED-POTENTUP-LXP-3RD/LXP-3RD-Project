package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.domain.Reviews;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewsJpaRepository extends JpaRepository<Reviews, Long> {
    Boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    Optional<Reviews> findByUserIdAndCourseId(Long userId, Long courseId);
    Slice<Reviews> findByCourseId(Long courseId, Pageable pageable);
    Long deleteByUserIdAndCourseId(Long userId, Long courseId);
}

package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.constant.ReviewStatus;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewsJpaRepository extends JpaRepository<Reviews, Long> {
    Boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    Slice<Reviews> findByCourseIdAndStatus(Long courseId, ReviewStatus status, Pageable pageable);
    Integer countByCourseId(Long courseId);
    Optional<Reviews> findByUserIdAndCourseIdAndStatus(Long userId, Long courseId, ReviewStatus status);
    Long deleteByUserIdAndCourseId(Long userId, Long courseId);
}

package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.domain.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewsJpaRepository extends JpaRepository<Reviews, Long> {
    Boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}

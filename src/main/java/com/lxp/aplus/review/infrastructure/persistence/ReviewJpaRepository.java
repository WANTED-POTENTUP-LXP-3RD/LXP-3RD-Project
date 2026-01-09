package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
    Boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}

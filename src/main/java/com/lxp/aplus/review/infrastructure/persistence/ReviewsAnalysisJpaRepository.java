package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.domain.ReviewsAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewsAnalysisJpaRepository extends JpaRepository<ReviewsAnalysis, Long> {
}

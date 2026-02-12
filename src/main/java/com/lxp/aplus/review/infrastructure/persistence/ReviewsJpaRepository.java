package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.constant.ReviewStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewAnalyzeItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewsJpaRepository extends JpaRepository<Reviews, Long>, CustomReviewRepository {
    Boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    Slice<Reviews> findByCourseIdAndStatus(Long courseId, ReviewStatus status, Pageable pageable);
    Integer countByCourseId(Long courseId);
    Optional<Reviews> findByUserIdAndCourseIdAndStatus(Long userId, Long courseId, ReviewStatus status);
    Long deleteByUserIdAndCourseId(Long userId, Long courseId);
    List<Reviews> findByUserIdAndCourseIdIn(Long userId, List<Long> courseIds);

    @Query("""
        select new com.lxp.aplus.review.infrastructure.persistence.dto.ReviewAnalyzeItem(r.rating, r.content)
        from Reviews r
        where r.courseId = :courseId
          and r.createdAt >= :from
    """)
    List<ReviewAnalyzeItem> findAnalyzeItems(Long courseId, LocalDateTime from);
}

package com.lxp.aplus.review.domain;

import com.lxp.aplus.review.infrastructure.dto.ReviewWroteDto;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewAnalyzeItem;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewsRepository {
    Reviews save(Reviews reviews);
    Boolean existsOwnReviewInCourse(Long id, Long courseId);
    Optional<Reviews> getReview(Long userId, Long courseId);
    Slice<Reviews> getCourseReviews(Long courseId, Pageable pageable);
    Integer countCourseReviews(Long courseId);
    void deleteReview(Reviews review);
    List<ReviewWroteDto> checkReviewedByCourseIds(List<Long> courseIds, Long userId);
    List<ReviewSummary> getReviewInfoInCourse(List<Long> courseId);
    List<ReviewAnalyzeItem> findAnalyzeItems(Long courseId, LocalDateTime from);
}

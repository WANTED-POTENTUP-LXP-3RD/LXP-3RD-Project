package com.lxp.aplus.review.domain;

import com.lxp.aplus.review.infrastructure.dto.ReviewWroteDto;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewStats;
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
    Long deleteReview(Long userId, Long courseId);
    List<ReviewWroteDto> checkReviewedByCourseIds(List<Long> courseIds, Long userId);
    List<ReviewStats> getReviewInfoInCourse(List<Long> courseId);
}

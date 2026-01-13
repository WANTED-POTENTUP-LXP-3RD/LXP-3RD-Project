package com.lxp.aplus.review.domain;

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
}

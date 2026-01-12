package com.lxp.aplus.review.domain;

import java.util.Optional;

public interface ReviewsRepository {
    Reviews save(Reviews reviews);
    Boolean existsOwnReviewInCourse(Long id, Long courseId);
    Optional<Reviews> getReview(Long reviewId);
}

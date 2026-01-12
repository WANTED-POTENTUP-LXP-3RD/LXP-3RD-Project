package com.lxp.aplus.review.domain;

public interface ReviewsRepository {
    Reviews save(Reviews reviews);
    Boolean existsOwnReviewInCourse(Long id, Long courseId);
}

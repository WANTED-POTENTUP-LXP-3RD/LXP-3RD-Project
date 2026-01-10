package com.lxp.aplus.review.domain;

public interface ReviewRepository {
    Review save(Review review);
    Boolean existsOwnReviewInCourse(Long id, Long courseId);
}

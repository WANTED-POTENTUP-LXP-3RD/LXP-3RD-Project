package com.lxp.aplus.review.domain;

public interface ReviewRepository {
    Reviews save(Reviews reviews);
    Boolean existsOwnReviewInCourse(Long id, Long courseId);
}

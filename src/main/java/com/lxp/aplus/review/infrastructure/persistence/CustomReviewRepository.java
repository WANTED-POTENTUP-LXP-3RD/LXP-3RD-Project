package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewSummary;
import java.util.List;

public interface CustomReviewRepository {
    public List<ReviewSummary> getReviewInfosInCourse(List<Long> courseId);
}

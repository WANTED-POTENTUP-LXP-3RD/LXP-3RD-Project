package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewStats;
import java.util.List;

public interface CustomReviewRepository {
    public List<ReviewStats> getReviewInfosInCourse(List<Long> courseId);
}

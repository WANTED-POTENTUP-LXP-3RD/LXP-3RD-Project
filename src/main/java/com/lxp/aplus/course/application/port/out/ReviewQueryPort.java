package com.lxp.aplus.course.application.port.out;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewSummary;
import java.util.List;

public interface ReviewQueryPort {
    public List<ReviewSummary> getReviewInfos(List<Long> courseId);
}

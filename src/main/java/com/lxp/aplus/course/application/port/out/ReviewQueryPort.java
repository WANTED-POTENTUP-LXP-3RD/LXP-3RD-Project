package com.lxp.aplus.course.application.port.out;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewStats;
import java.util.List;

public interface ReviewQueryPort {
    public List<ReviewStats> getReviewInfos(List<Long> courseId);
}

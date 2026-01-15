package com.lxp.aplus.course.infrastructure.adapter;

import com.lxp.aplus.course.application.port.out.ReviewQueryPort;
import com.lxp.aplus.review.domain.ReviewsRepository;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewStats;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewQueryAdapter implements ReviewQueryPort {
    private final ReviewsRepository reviewsRepository;

    @Override
    public List<ReviewStats> getReviewInfos(List<Long> courseIds) {
        return reviewsRepository.getReviewInfoInCourse(courseIds);
    }
}

package com.lxp.aplus.review.infrastructure.persistence;

import static com.lxp.aplus.review.domain.QReviews.reviews;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewSummary;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final JPAQueryFactory queryFactory;

    public List<ReviewSummary> getReviewInfosInCourse(List<Long> courseIds) {
        return queryFactory
                .select(Projections.constructor(ReviewSummary.class,
                        reviews.courseId,
                        reviews.rating.avg(),
                        reviews.id.count()
                ))
                .from(reviews)
                .where(reviews.courseId.in(courseIds))
                .groupBy(reviews.courseId)
                .fetch();
    }
}

package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewsRepositoryImpl implements ReviewsRepository {
    private final ReviewsJpaRepository reviewJpaRepository;

    @Override
    public Reviews save(Reviews review) {
        return reviewJpaRepository.save(review);
    }

    @Override
    public Boolean existsOwnReviewInCourse(Long userId, Long courseId) {
        return reviewJpaRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Override
    public Optional<Reviews> getReview(Long userId, Long courseId) {
        return reviewJpaRepository.findByUserIdAndCourseId(userId, courseId);
    }

    @Override
    public Slice<Reviews> getCourseReviews(Long courseId, Pageable pageable) {
        return reviewJpaRepository.findByCourseId(courseId, pageable);
    }
}

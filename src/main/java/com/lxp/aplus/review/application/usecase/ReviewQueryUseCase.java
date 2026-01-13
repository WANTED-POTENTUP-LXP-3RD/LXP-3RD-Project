package com.lxp.aplus.review.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.application.command.ReviewQueryCommand;
import com.lxp.aplus.review.application.result.ReviewResult;
import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewQueryUseCase {
    private final ReviewsRepository reviewRepository;

    public ReviewResult findReviewWithCourseId(ReviewQueryCommand command) {
        Reviews review = reviewRepository.getReview(command.userId(), command.courseId())
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));
        return ReviewResult.of(command.userId(), review);
    }

    public Slice<ReviewResult> findReviewsInCourse(Long userId, Long courseId, Pageable pageable) {
        Slice<Reviews> reviews = reviewRepository.getCourseReviews(courseId,pageable);
        return reviews.map(review -> ReviewResult.of(userId, review));
    }
}

package com.lxp.aplus.review.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.application.command.ReviewQueryCommand;
import com.lxp.aplus.review.application.port.out.UserQueryPort;
import com.lxp.aplus.review.application.result.ReviewResult;
import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewQueryUseCase {
    private final ReviewsRepository reviewRepository;
    private final UserQueryPort userQueryPort;

    public ReviewResult findReviewWithCourseId(ReviewQueryCommand command) {
        Reviews review = reviewRepository.getReview(command.userId(), command.courseId())
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        String nickName = userQueryPort.findUserName(command.userId());

        return ReviewResult.of(command.userId(), nickName, review);
    }

    public Slice<ReviewResult> findReviewsInCourse(ReviewQueryCommand command, Pageable pageable) {
        Slice<Reviews> reviews = reviewRepository.getCourseReviews(command.courseId(), pageable);
        String nickName = userQueryPort.findUserName(command.userId());

        return reviews.map(review -> ReviewResult.of(command.userId(), nickName, review));
    }

    public Integer countReviewsInCourse(Long courseId) {
        return reviewRepository.countCourseReviews(courseId);
    }
}

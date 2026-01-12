package com.lxp.aplus.review.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.application.command.ReviewCreateCommand;
import com.lxp.aplus.review.application.command.ReviewQueryCommand;
import com.lxp.aplus.review.application.command.ReviewUpdateCommand;
import com.lxp.aplus.review.application.policy.ReviewBusinessPolicy;
import com.lxp.aplus.review.application.result.ReviewResult;
import com.lxp.aplus.review.application.result.ReviewUpsertResult;
import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandUseCase {
    private final ReviewBusinessPolicy policy;
    private final ReviewsRepository reviewRepository;

    /**
     * 새로운 리뷰를 등록합니다.
     *
     * @param command 리뷰 등록에 필요한 데이터 (courseId, userId, rating, content)
     * @return 등록된 리뷰 결과 DTO
     */
    public ReviewUpsertResult createReview(ReviewCreateCommand command) {
        policy.validateCreateReview(command.userId(), command.courseId());

        Reviews savedReview = reviewRepository.save(command.toEntity());

        return ReviewUpsertResult.from(savedReview);
    }

    public ReviewUpsertResult updateReview(ReviewUpdateCommand command) {
        Reviews review = reviewRepository.getReview(command.userId(), command.courseId())
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        policy.validateUpdateReview(command.userId(), review);

        review.update(command.rating(), command.content(), command.userId());

        Reviews result = reviewRepository.save(review);

        return ReviewUpsertResult.from(result);
    }

    public ReviewResult findReviewWithCourseId(ReviewQueryCommand command) {
        Reviews review = reviewRepository.getReview(command.userId(), command.courseId())
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));
        return ReviewResult.from(review);
    }

    public List<ReviewResult> findReviewsInCourse(Long courseId) {
        List<Reviews> reviews = reviewRepository.getCourseReviews(courseId);
        return reviews.stream().map(ReviewResult::from).toList();
    }
}

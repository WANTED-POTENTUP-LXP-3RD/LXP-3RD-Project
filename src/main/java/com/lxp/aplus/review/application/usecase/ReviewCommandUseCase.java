package com.lxp.aplus.review.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.application.command.ReviewAnalyzeSaveCommand;
import com.lxp.aplus.review.application.command.ReviewCreateCommand;
import com.lxp.aplus.review.application.command.ReviewDeleteCommand;
import com.lxp.aplus.review.application.command.ReviewUpdateCommand;
import com.lxp.aplus.review.application.policy.ReviewBusinessPolicy;
import com.lxp.aplus.review.application.port.out.ReviewAnalyzeSavePort;
import com.lxp.aplus.review.application.result.ReviewDeleteResult;
import com.lxp.aplus.review.application.result.ReviewUpsertResult;
import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandUseCase {
    private final ReviewBusinessPolicy policy;
    private final ReviewsRepository reviewRepository;
    private final ReviewAnalyzeSavePort reviewAnalyzeSavePort;

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

    public ReviewDeleteResult deleteReview(ReviewDeleteCommand command) {
        Reviews review = reviewRepository.getReview(command.userId(), command.courseId())
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        reviewRepository.deleteReview(review);

        return new ReviewDeleteResult(true);
    }

    /**
     * 리뷰 인사이트 분석 결과를 저장합니다.
     * - Query 트랜잭션(readOnly=true)에서 호출되더라도
     *   저장은 반드시 write 트랜잭션으로 분리되어야 함.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void saveReviewAnalysis(ReviewAnalyzeSaveCommand command) {
        reviewAnalyzeSavePort.save(command);
    }
}

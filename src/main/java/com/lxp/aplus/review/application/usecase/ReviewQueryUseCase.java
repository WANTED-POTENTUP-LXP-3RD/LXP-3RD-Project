package com.lxp.aplus.review.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.application.command.ReviewQueryCommand;
import com.lxp.aplus.review.application.port.out.UserQueryPort;
import com.lxp.aplus.review.application.result.ReviewResult;
import com.lxp.aplus.review.application.result.ReviewWroteResult;
import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;
import com.lxp.aplus.review.infrastructure.dto.ReviewWroteDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

    public List<ReviewWroteResult> checkReviewed(List<Long> courseIds, Long userId){
        List<ReviewWroteDto> result = reviewRepository.checkReviewedByCourseIds(courseIds, userId);

        Set<Long> writtenIds = result.stream()
                .map(ReviewWroteDto::courseId)
                .collect(Collectors.toSet());

        return courseIds.stream()
                .map(id -> new ReviewWroteResult(
                        id,
                        writtenIds.contains(id) // 작성 목록에 있으면 true, 없으면 false
                ))
                .toList();
    }
}

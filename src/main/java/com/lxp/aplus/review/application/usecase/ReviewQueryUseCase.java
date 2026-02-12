package com.lxp.aplus.review.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.application.command.ReviewAnalyzeSaveCommand;
import com.lxp.aplus.review.application.command.ReviewQueryCommand;
import com.lxp.aplus.review.application.result.ReviewAnalysisAiResult;
import com.lxp.aplus.review.application.port.out.UserQueryPort;
import com.lxp.aplus.review.application.port.out.ReviewAnalysisAiPort;
import com.lxp.aplus.review.application.result.ReviewForAnalysisResult;
import com.lxp.aplus.review.application.result.ReviewResult;
import com.lxp.aplus.review.application.result.ReviewWroteResult;
import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;
import com.lxp.aplus.review.infrastructure.dto.ReviewWroteDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewAnalyzeItem;
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
    private final ReviewAnalysisAiPort reviewAnalysisAiPort;
    private final ReviewCommandUseCase reviewCommandUseCase;

    public ReviewResult findReviewWithCourseId(ReviewQueryCommand command) {
        Reviews review = reviewRepository.getReview(command.userId(), command.courseId())
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        String nickName = userQueryPort.findUserNames(List.of(command.userId())).get(command.userId());

        return ReviewResult.of(command.userId(), nickName, review);
    }

    public Slice<ReviewResult> findReviewsInCourse(ReviewQueryCommand command, Pageable pageable) {
        Slice<Reviews> reviews = reviewRepository.getCourseReviews(command.courseId(), pageable);
        List<Long> writerIds = reviews.stream()
                .map(Reviews::getUserId)
                .distinct()
                .toList();

        Map<Long, String> writerNicknames = userQueryPort.findUserNames(writerIds);


        return reviews.map(review -> {
            String writerName = writerNicknames.getOrDefault(review.getUserId(), "알 수 없음");

            return ReviewResult.of(command.userId(), writerName, review);
        });
    }

    public Integer countReviewsInCourse(Long courseId) {
        return reviewRepository.countCourseReviews(courseId);
    }

    public List<ReviewWroteResult> checkReviewed(List<Long> courseIds, Long userId) {
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

    public ReviewForAnalysisResult findReviewForAnalysis(ReviewQueryCommand command) {

        // 1) days 기준 리뷰 목록 조회
        LocalDateTime from = LocalDateTime.now().minusDays(command.day());
        List<ReviewAnalyzeItem> items = reviewRepository.findAnalyzeItems(command.courseId(), from);

        if (items.isEmpty()) {
            // 팀 정책에 맞게: 404 or 200(데이터 없음) 결정 필요
            throw new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND);
        }

        // 2) 평균 rating 계산
        Double avgRating = items.stream()
                .mapToInt(ReviewAnalyzeItem::rating)
                .average()
                .orElse(0.0);

        // 3) Python 호출
        ReviewAnalysisAiResult aiResult = reviewAnalysisAiPort.analyze(items);

        // 4) 분석 결과 저장 (재사용 로직 없음 => 무조건 insert)
        reviewCommandUseCase.saveReviewAnalysis(ReviewAnalyzeSaveCommand.of(
                command.courseId(),
                command.day(),
                aiResult.mood(),
                aiResult.insightSummary()
        ));

        // 5) 응답
        return ReviewForAnalysisResult.of(avgRating, aiResult);
    }
}

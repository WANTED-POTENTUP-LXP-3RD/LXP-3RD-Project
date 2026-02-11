package com.lxp.aplus.review.presentation;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.ReviewResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.CurrentUser;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.review.application.command.ReviewDeleteCommand;
import com.lxp.aplus.review.application.command.ReviewQueryCommand;
import com.lxp.aplus.review.application.result.ReviewDeleteResult;
import com.lxp.aplus.review.application.result.ReviewForAnalysisResult;
import com.lxp.aplus.review.application.result.ReviewResult;
import com.lxp.aplus.review.application.result.ReviewUpsertResult;
import com.lxp.aplus.review.application.usecase.ReviewCommandUseCase;
import com.lxp.aplus.review.application.usecase.ReviewQueryUseCase;
import com.lxp.aplus.review.presentation.request.ReviewCreateRequest;
import com.lxp.aplus.review.presentation.request.ReviewFindCourseIdRequest;
import com.lxp.aplus.review.presentation.request.ReviewUpdateRequest;
import com.lxp.aplus.review.presentation.response.ReviewAnalyzeResponse;
import com.lxp.aplus.review.presentation.response.ReviewDeleteResponse;
import com.lxp.aplus.review.presentation.response.ReviewResponse;
import com.lxp.aplus.review.presentation.response.ReviewUpsertResponse;
import com.lxp.aplus.review.presentation.response.ReviewWroteResponse;
import com.lxp.aplus.review.presentation.response.SliceResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewCommandUseCase reviewCommandUseCase;
    private final ReviewQueryUseCase reviewQueryUseCase;

    @PostMapping("courses/{courseId}/reviews")
    public ResponseEntity<ResultResponse<ReviewUpsertResponse>> uploadReview(
            @Valid @RequestBody ReviewCreateRequest request,
            @PathVariable Long courseId,
            @Authenticated Long userId
    ) {
        ReviewUpsertResult result = reviewCommandUseCase.createReview(request.toCommand(userId, courseId));
        return ResponseEntity.status(ReviewResultCode.REVIEW_CREATE_SUCCESS.getStatus())
                .body(ResultResponse.of(ReviewResultCode.REVIEW_CREATE_SUCCESS, ReviewUpsertResponse.from(result)));
    }

    @PatchMapping("courses/{courseId}/reviews")
    public ResponseEntity<ResultResponse<ReviewUpsertResponse>> updateReview(
            @PathVariable Long courseId,
            @Valid @RequestBody ReviewUpdateRequest request,
            @Authenticated Long userId
    ) {
        ReviewUpsertResult result = reviewCommandUseCase.updateReview(request.toCommand(userId, courseId));
        return ResponseEntity.status(ReviewResultCode.REVIEW_UPDATE_SUCCESS.getStatus())
                .body(ResultResponse.of(ReviewResultCode.REVIEW_UPDATE_SUCCESS, ReviewUpsertResponse.from(result)));
    }

    @GetMapping("/courses/{courseId}/review")
    public ResponseEntity<ResultResponse<ReviewResponse>> getSimpleReview(
            @Authenticated Long userId,
            @PathVariable Long courseId
    ) {
        ReviewResult result = reviewQueryUseCase.findReviewWithCourseId(ReviewQueryCommand.of(userId, courseId));
        return ResponseEntity.status(ReviewResultCode.REVIEW_FIND_SUCCESS.getStatus())
                .body(ResultResponse.of(ReviewResultCode.REVIEW_FIND_SUCCESS, ReviewResponse.from(result)));
    }

    @GetMapping("/courses/{courseId}/reviews")
    public ResponseEntity<ResultResponse<SliceResponse<ReviewResponse>>> getAllReviews(
            @PathVariable Long courseId,
            @CurrentUser UserInfo userInfo,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        Long userId = (userInfo != null) ? userInfo.id() : null;

        Slice<ReviewResponse> result = reviewQueryUseCase.findReviewsInCourse(ReviewQueryCommand.of(userId, courseId),
                        pageable)
                .map(ReviewResponse::from);

        Integer totalCount = reviewQueryUseCase.countReviewsInCourse(courseId);

        SliceResponse<ReviewResponse> response = SliceResponse.of(result, totalCount);

        return ResponseEntity.status(ReviewResultCode.REVIEW_FIND_SUCCESS.getStatus())
                .body(ResultResponse.of(ReviewResultCode.REVIEW_FIND_SUCCESS, response));
    }

    @DeleteMapping("/courses/{courseId}/review")
    public ResponseEntity<ResultResponse<ReviewDeleteResponse>> deleteReview(
            @PathVariable Long courseId,
            @Authenticated Long userId
    ) {
        ReviewDeleteResult result = reviewCommandUseCase.deleteReview(ReviewDeleteCommand.of(userId, courseId));

        return ResponseEntity.status(ReviewResultCode.REVIEW_DELETE_SUCCESS.getStatus())
                .body(ResultResponse.of(ReviewResultCode.REVIEW_DELETE_SUCCESS, ReviewDeleteResponse.from(result)));
    }

    @PostMapping("/reviews/my")
    public ResponseEntity<ResultResponse<List<ReviewWroteResponse>>> getMyReviewsIsWrote(
            @RequestBody ReviewFindCourseIdRequest request, @Authenticated Long userId) {
        List<ReviewWroteResponse> result = reviewQueryUseCase.checkReviewed(request.courseIds(), userId).stream().map(ReviewWroteResponse::from).toList();
        return ResponseEntity.status(ReviewResultCode.REVIEW_FIND_SUCCESS.getStatus())
                .body(ResultResponse.of(ReviewResultCode.REVIEW_FIND_SUCCESS, result));
    }

    /**
     * 강좌별 리뷰를 최근 N일(day) 기준으로 조회해 AI 분석(분위기 + 한 줄 요약)을 수행한 뒤 결과를 반환합니다.
     *
     * - courseId: 분석 대상 강좌 ID
     * - day: 최근 N일 기준(기본 7일)
     * - userId: 인증된 사용자 ID
     *
     * 성공 시 평균 별점과 AI 분석 결과를 포함한 응답을 반환합니다.
     */
    @GetMapping("/courses/{courseId}/review/summary")
    public ResponseEntity<ResultResponse<ReviewAnalyzeResponse>> analyzeReviews(
            @PathVariable Long courseId,
            @RequestParam(name = "day", defaultValue = "7") int day,
            @Authenticated Long userId
    ) {
        ReviewForAnalysisResult result = reviewQueryUseCase.findReviewForAnalysis(
                ReviewQueryCommand.of(userId, courseId, day));

        return ResponseEntity.status(ReviewResultCode.REVIEW_ANALYZE_SUCCESS.getStatus())
                .body(ResultResponse.of(ReviewResultCode.REVIEW_ANALYZE_SUCCESS, ReviewAnalyzeResponse.from(result)));
    }
}

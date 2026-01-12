package com.lxp.aplus.review.presentation;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.ReviewResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.review.application.result.ReviewUpsertResult;
import com.lxp.aplus.review.application.usecase.ReviewCommandUseCase;
import com.lxp.aplus.review.presentation.request.ReviewCreateRequest;
import com.lxp.aplus.review.presentation.request.ReviewUpdateRequest;
import com.lxp.aplus.review.presentation.response.ReviewUpsertResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewCommandUseCase reviewCommandUseCase;

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

    @PatchMapping("courses/{courseId}/reviews/{reviewId}")
    public ResponseEntity<ResultResponse<ReviewUpsertResponse>> updateReview(
            @PathVariable Long courseId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request,
            @Authenticated Long userId
    ) {
        ReviewUpsertResult result = reviewCommandUseCase.updateReview(request.toCommand(userId,courseId,reviewId));
        return ResponseEntity.status(ReviewResultCode.REVIEW_UPDATE_SUCCESS.getStatus())
                .body(ResultResponse.of(ReviewResultCode.REVIEW_UPDATE_SUCCESS, ReviewUpsertResponse.from(result)));
    }
}

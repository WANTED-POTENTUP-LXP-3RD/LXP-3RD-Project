package com.lxp.aplus.review.presentation;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.ReviewResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.CurrentUser;
import com.lxp.aplus.common.security.InstructorOnly;
import com.lxp.aplus.review.application.result.ReviewUpsertResult;
import com.lxp.aplus.review.application.usecase.ReviewCommandUseCase;
import com.lxp.aplus.review.presentation.request.ReviewCreateRequest;
import com.lxp.aplus.review.presentation.response.ReviewUpsertResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewCommandUseCase reviewCommandUseCase;

//    @Authenticated -> 로그인 정보 필요할 때
//    @CurrentUser -> 비로그인 상태로 돌아댕길 때
//    @InstructorOnly -> 강사권한 필요할 때
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
}

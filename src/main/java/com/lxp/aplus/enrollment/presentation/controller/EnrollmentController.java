package com.lxp.aplus.enrollment.presentation.controller;

import com.lxp.aplus.common.result.PageResponse;
import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.EnrollmentResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.enrollment.application.result.EnrollmentDetailResult;
import com.lxp.aplus.enrollment.application.result.EnrollmentListItemResult;
import com.lxp.aplus.enrollment.application.usecase.EnrollmentQueryUseCase;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.enrollment.presentation.response.EnrollmentDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/enrollments")
public class EnrollmentController {

    private final EnrollmentQueryUseCase enrollmentQueryUseCase;

    @GetMapping
    public ResponseEntity<ResultResponse<PageResponse<EnrollmentListItemResult>>> getEnrollmentList(
            @Authenticated UserInfo currentUser,
            @RequestParam(required = false, defaultValue = "ENROLLED") EnrollmentStatus status,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Page<EnrollmentListItemResult> result = enrollmentQueryUseCase.getEnrollmentList(currentUser.id(), status, pageable);
        return ResponseEntity.ok(
                ResultResponse.of(EnrollmentResultCode.GET_ENROLLMENTS_SUCCESS, PageResponse.from(result))
        );
    }

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<ResultResponse<EnrollmentDetailResponse>> getEnrollmentDetail(
            @Authenticated UserInfo currentUser,
            @PathVariable Long enrollmentId
    ) {
        EnrollmentDetailResult result = enrollmentQueryUseCase.getEnrollmentDetail(currentUser.id(), enrollmentId);
        return ResponseEntity.ok(
                ResultResponse.of(EnrollmentResultCode.GET_ENROLLMENT_DETAIL_SUCCESS, EnrollmentDetailResponse.from(result))
        );
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ResultResponse<EnrollmentDetailResponse>> getEnrollmentDetailByCourseId(
            @Authenticated UserInfo currentUser,
            @PathVariable Long courseId
    ) {
        EnrollmentDetailResult result = enrollmentQueryUseCase.getEnrollmentDetailByCourseId(currentUser.id(), courseId);
        return ResponseEntity.ok(
                ResultResponse.of(EnrollmentResultCode.GET_ENROLLMENT_DETAIL_SUCCESS, EnrollmentDetailResponse.from(result))
        );
    }
}

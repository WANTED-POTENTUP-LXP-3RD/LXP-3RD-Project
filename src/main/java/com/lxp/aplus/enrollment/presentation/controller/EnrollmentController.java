package com.lxp.aplus.enrollment.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.EnrollmentResultCode;
import com.lxp.aplus.enrollment.application.result.EnrollmentListQueryResult;
import com.lxp.aplus.enrollment.application.usecase.EnrollmentQueryUseCase;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/enrollments")
public class EnrollmentController {

    private final EnrollmentQueryUseCase enrollmentQueryUseCase;


    @GetMapping
    public ResponseEntity<ResultResponse<EnrollmentListQueryResult>> getEnrollmentList(
            @RequestParam(required = false, defaultValue = "ENROLLED") EnrollmentStatus status,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Long studentId = 1L;
        EnrollmentListQueryResult result = enrollmentQueryUseCase.getEnrollmentList(studentId, status, pageable);
        return ResponseEntity.ok(
                ResultResponse.of(EnrollmentResultCode.GET_ENROLLMENTS_SUCCESS, result)
        );
    }
}

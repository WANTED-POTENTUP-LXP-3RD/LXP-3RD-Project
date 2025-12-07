package com.lxp.aplus.enrollment.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.EnrollmentResultCode;
import com.lxp.aplus.enrollment.application.result.EnrollmentCreationResult;
import com.lxp.aplus.enrollment.application.usecase.EnrollmentCommandUseCase;
import com.lxp.aplus.enrollment.presentation.request.EnrollmentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/enrollments")
public class EnrollmentController {

    private final EnrollmentCommandUseCase enrollmentCommandUseCase;

    @PostMapping
    public ResponseEntity<ResultResponse<EnrollmentCreationResult>> enroll(
            //@AuthenticationPrincipal Long studentId,
            @Valid @RequestBody EnrollmentRequest request) {
        Long studentId = 1L; // TODO: 인증 기능 구현 후 수정 예정

        EnrollmentCreationResult enrollmentCreationResult = enrollmentCommandUseCase.enroll(request.toCommand(studentId));

        return ResponseEntity.ok(
                ResultResponse.of(EnrollmentResultCode.ENROLLMENT_SUCCESS, enrollmentCreationResult)
        );
    }
}

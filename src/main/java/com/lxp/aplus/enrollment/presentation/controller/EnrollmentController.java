package com.lxp.aplus.enrollment.presentation.controller;

import com.lxp.aplus.enrollment.application.usecase.EnrollmentCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/enrollments")
public class EnrollmentController {

    private final EnrollmentCommandUseCase enrollmentCommandUseCase;

}

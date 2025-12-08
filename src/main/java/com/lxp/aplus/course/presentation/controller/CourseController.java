package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.CourseResultCode;
import com.lxp.aplus.course.application.result.CourseResult;
import com.lxp.aplus.course.application.usecase.CourseCommandUseCase;
import com.lxp.aplus.course.presentation.request.CourseCreateRequest;
import com.lxp.aplus.course.presentation.request.CourseUpdateRequest;
import com.lxp.aplus.course.presentation.response.CourseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseCommandUseCase courseCommandUseCase;

    @PostMapping("/api/instructor/courses")
    public ResponseEntity<ResultResponse<CourseResponse>> createCourse(@RequestParam Long instructorId,
                                                                       @Valid @RequestBody CourseCreateRequest request) {
        CourseResult courseResult = courseCommandUseCase.createCourse(instructorId, request.toCommand());

        return ResponseEntity
                .status(CourseResultCode.COURSE_REGISTER_SUCCESS.getStatus())
                .body(ResultResponse.of(
                        CourseResultCode.COURSE_REGISTER_SUCCESS,
                        CourseResponse.from(courseResult))
                );
    }

    @PatchMapping("/api/instructor/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseResponse>> updateCourse(@PathVariable Long courseId,
                                                                       @RequestParam Long instructorId,
                                                                       @RequestBody CourseUpdateRequest request) {
        CourseResult courseResult = courseCommandUseCase.updateCourse(courseId, instructorId, request.toCommand());

        return ResponseEntity
                .status(CourseResultCode.COURSE_UPDATE_SUCCESS.getStatus())
                .body(ResultResponse.of(
                        CourseResultCode.COURSE_UPDATE_SUCCESS,
                        CourseResponse.from(courseResult))
                );
    }
}

package com.lxp.aplus.course.presentation.controller;


import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.LectureResultCode;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.application.usecase.LectureCommandUseCase;
import com.lxp.aplus.course.presentation.request.LectureCreateRequest;
import com.lxp.aplus.course.presentation.response.LectureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureController {

    private final LectureCommandUseCase lectureCommandUseCase;

    @PostMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures")
    public ResponseEntity<ResultResponse<LectureResponse>> createLecture (@PathVariable Long courseId, @PathVariable Long sectionId, @RequestBody LectureCreateRequest request) {
        LectureResult result = lectureCommandUseCase.createLecture(courseId, sectionId, request.toCommand());

        return ResponseEntity
                .status(LectureResultCode.LECTURE_REGISTER_SUCCESS.getStatus())

                .body(ResultResponse.of(
                        LectureResultCode.LECTURE_REGISTER_SUCCESS,
                        LectureResponse.from(result)));
    }
}

package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.LectureResultCode;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.application.usecase.LectureCommandUseCase;
import com.lxp.aplus.course.application.usecase.LectureQueryUseCase;
import com.lxp.aplus.course.presentation.request.LectureCreateRequest;
import com.lxp.aplus.course.presentation.request.LectureUpdateRequest;
import com.lxp.aplus.course.presentation.response.LectureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureController {

    private final LectureCommandUseCase lectureCommandUseCase;
    private final LectureQueryUseCase lectureQueryUseCase;

    /** 강의 생성 **/
    @PostMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures")
    public ResponseEntity<ResultResponse<LectureResponse>> createLecture (@PathVariable Long courseId, @PathVariable Long sectionId, @RequestBody LectureCreateRequest request) {
        LectureResult result = lectureCommandUseCase.createLecture(courseId, sectionId, request.toCommand());

        return ResponseEntity
                .status(LectureResultCode.LECTURE_REGISTER_SUCCESS.getStatus())

                .body(ResultResponse.of(
                        LectureResultCode.LECTURE_REGISTER_SUCCESS,
                        LectureResponse.from(result)));
    }

    /** 강의 상세 조회 **/
    @GetMapping("/instructor/courses/{courseId}/lectures/{lectureId}")
    public ResponseEntity<ResultResponse<LectureResponse>> getLectureDetail(@PathVariable Long courseId, @PathVariable Long lectureId) {
        LectureResult result = lectureQueryUseCase.getLectureDetails(courseId, lectureId);

        return ResponseEntity
                .status(LectureResultCode.LECTURE_READ_SUCCESS.getStatus())
                .body(ResultResponse.of(
                        LectureResultCode.LECTURE_READ_SUCCESS,
                        LectureResponse.from(result)));
    }

    /** 강의 수정 **/
    @PutMapping("/instructor/courses/{courseId}/lectures/{lectureId}")
    public ResponseEntity<ResultResponse<LectureResponse>> updateLecture(@PathVariable Long courseId, @PathVariable Long lectureId, @RequestBody LectureUpdateRequest request) {
        LectureResult result = lectureCommandUseCase.updateLecture(courseId, lectureId, request.toCommand());

        return ResponseEntity
                .ok(ResultResponse.of(
                        LectureResultCode.LECTURE_UPDATE_SUCCESS,
                        LectureResponse.from(result)));
    }

    /** 강의 삭제 **/
    @DeleteMapping("/instructor/courses/{courseId}/lectures/{lectureId}")
    public ResponseEntity<ResultResponse<Void>> deleteLecture(@PathVariable Long courseId, @PathVariable Long lectureId) {
        lectureCommandUseCase.deleteLecture(courseId, lectureId);

        return ResponseEntity
                .ok(ResultResponse.of(
                        LectureResultCode.LECTURE_DELETE_SUCCESS,
                        null));
    }
}

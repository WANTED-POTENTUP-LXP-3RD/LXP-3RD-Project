package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.LectureResultCode;
import com.lxp.aplus.common.security.InstructorOnly;
import com.lxp.aplus.course.application.command.CreateLectureCommand;
import com.lxp.aplus.course.application.command.UpdateLectureCommand;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.application.usecase.LectureCommandUseCase;
import com.lxp.aplus.course.application.usecase.LectureQueryUseCase;
import com.lxp.aplus.course.application.vo.UploadFile;
import com.lxp.aplus.course.presentation.request.LectureCreateRequest;
import com.lxp.aplus.course.presentation.request.LectureUpdateRequest;
import com.lxp.aplus.course.presentation.response.LectureResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureController {

    private final LectureCommandUseCase lectureCommandUseCase;
    private final LectureQueryUseCase lectureQueryUseCase;

    /** 강의 생성 **/
    @InstructorOnly
    @PostMapping(
            value = "/instructor/courses/{courseId}/sections/{sectionId}/lectures",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse<LectureResponse>> createLecture (@Valid @PathVariable Long courseId,
                                                                          @PathVariable Long sectionId,
                                                                          @RequestPart("lecture") LectureCreateRequest request,
                                                                          @RequestPart("multiFile") MultipartFile file) throws IOException {

        UploadFile uploadFile = UploadFile.from(file);

        CreateLectureCommand command = request.toCommand(uploadFile);

        LectureResult result = lectureCommandUseCase.createLecture(courseId, sectionId, command);

        return ResponseEntity
                .status(LectureResultCode.LECTURE_REGISTER_SUCCESS.getStatus())

                .body(ResultResponse.of(
                        LectureResultCode.LECTURE_REGISTER_SUCCESS,
                        LectureResponse.from(result)));
    }

    /** 강의 상세 조회 **/
    @InstructorOnly
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
    @InstructorOnly
    @PutMapping(
            value = "/instructor/courses/{courseId}/lectures/{lectureId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse<LectureResponse>> updateLecture(@Valid @PathVariable Long courseId, @PathVariable Long lectureId,
                                                                         @RequestPart("lecture") LectureUpdateRequest request,
                                                                         @RequestPart(value = "multiFile", required = false) MultipartFile file) throws IOException{

        UploadFile uploadFile = null;

        // 파일 교체 + 메타 정보 교체
        if (file != null || !file.isEmpty()) {
            uploadFile = UploadFile.from(file);
        }

        UpdateLectureCommand command = request.toCommand(uploadFile);

        LectureResult result = lectureCommandUseCase.updateLecture(courseId, lectureId, command);

        return ResponseEntity
                .status(LectureResultCode.LECTURE_UPDATE_SUCCESS.getStatus())
                .body(ResultResponse.of(
                        LectureResultCode.LECTURE_UPDATE_SUCCESS,
                        LectureResponse.from(result)));
    }

    /** 강의 삭제 **/
    @InstructorOnly
    @DeleteMapping("/instructor/courses/{courseId}/lectures/{lectureId}")
    public ResponseEntity<ResultResponse<Void>> deleteLecture(@PathVariable Long courseId, @PathVariable Long lectureId) {
        lectureCommandUseCase.deleteLecture(courseId, lectureId);

        return ResponseEntity
                .status(LectureResultCode.LECTURE_DELETE_SUCCESS.getStatus())
                .body(ResultResponse.from(LectureResultCode.LECTURE_DELETE_SUCCESS));
    }
}

package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.LectureResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.InstructorOnly;
import com.lxp.aplus.course.application.result.PresignedUrlResult;
import com.lxp.aplus.course.application.usecase.LectureResourceCommandUseCase;
import com.lxp.aplus.course.presentation.request.PresignedUrlRequest;
import com.lxp.aplus.course.presentation.response.PresignedUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureResourceController {
    private final LectureResourceCommandUseCase useCase;

    /** 강의 리소스 presigned url **/
    @InstructorOnly
    @PostMapping("/instructor/resources")
    public ResponseEntity<ResultResponse<PresignedUrlResponse>> generatePresignedUrl(
            @Authenticated Long instructorId,
            @Valid @RequestBody PresignedUrlRequest request
    ) {
           PresignedUrlResult result = useCase.generatePresignedUrl(request.toCommand());

           return ResponseEntity.status(LectureResultCode.LECTURE_RESOURCE_REGISTER_SUCCESS.getStatus())
                   .body(ResultResponse.of(
                           LectureResultCode.LECTURE_RESOURCE_REGISTER_SUCCESS,
                           PresignedUrlResponse.from(result)
                   ));
    }
}

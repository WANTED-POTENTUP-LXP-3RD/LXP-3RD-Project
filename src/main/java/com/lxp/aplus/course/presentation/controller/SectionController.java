package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.SectionResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.InstructorOnly;
import com.lxp.aplus.course.application.usecase.CourseCommandUseCase;
import com.lxp.aplus.course.application.usecase.CourseQueryUseCase;
import com.lxp.aplus.course.presentation.request.SectionCreateRequest;
import com.lxp.aplus.course.presentation.request.SectionUpdateRequest;
import com.lxp.aplus.course.presentation.response.SectionUpsertResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SectionController {
    private final CourseCommandUseCase courseCommandUseCase;

    @InstructorOnly
    @PostMapping("/api/instructor/courses/{courseId}/sections")
    public ResponseEntity<ResultResponse<SectionUpsertResponse>> createSection(
            @Authenticated Long instructorId,
            @PathVariable("courseId") Long courseId,
            @Valid @RequestBody SectionCreateRequest request
    ) {
        SectionUpsertResponse response = courseCommandUseCase.createSection(courseId, instructorId, request.toCommand());

        return ResponseEntity
                .status(SectionResultCode.SECTION_REGISTER_SUCCESS.getStatus())
                .body(ResultResponse.of(SectionResultCode.SECTION_REGISTER_SUCCESS, response));
    }

    @InstructorOnly
    @PatchMapping("/api/instructor/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<ResultResponse<SectionUpsertResponse>> updateSection(
            @Authenticated Long instructorId,
            @PathVariable("courseId") Long courseId,
            @PathVariable("sectionId") Long sectionId,
            @RequestBody SectionUpdateRequest request
    ) {
        SectionUpsertResponse response = courseCommandUseCase.updateSection(courseId, instructorId, sectionId, request.toCommand());

        return ResponseEntity
                .status(SectionResultCode.SECTION_UPDATE_SUCCESS.getStatus())
                .body(ResultResponse.of(SectionResultCode.SECTION_UPDATE_SUCCESS, response));
    }

    @InstructorOnly
    @DeleteMapping("/api/instructor/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<ResultResponse<Void>> deleteSection(
            @Authenticated Long instructorId,
            @PathVariable("courseId") Long courseId,
            @PathVariable("sectionId") Long sectionId
    ) {
        courseCommandUseCase.deleteSection(courseId, instructorId, sectionId);

        return ResponseEntity
                .status(SectionResultCode.SECTION_DELETE_SUCCESS.getStatus())
                .body(ResultResponse.from(SectionResultCode.SECTION_DELETE_SUCCESS));
    }
}

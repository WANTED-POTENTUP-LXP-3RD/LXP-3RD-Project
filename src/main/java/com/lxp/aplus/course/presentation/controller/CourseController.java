package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.PageResponse;
import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.CourseResultCode;
import com.lxp.aplus.common.result.code.SectionResultCode;
import com.lxp.aplus.course.application.usecase.CourseCommandUseCase;
import com.lxp.aplus.course.application.usecase.CourseQueryUseCase;
import com.lxp.aplus.course.presentation.request.CourseCreateRequest;
import com.lxp.aplus.course.presentation.request.CourseUpdateRequest;
import com.lxp.aplus.course.presentation.request.SectionCreateRequest;
import com.lxp.aplus.course.presentation.request.SectionUpdateRequest;
import com.lxp.aplus.course.presentation.response.CourseDetailResponse;
import com.lxp.aplus.course.presentation.response.CourseResponse;
import com.lxp.aplus.course.presentation.response.CourseUpsertResponse;
import com.lxp.aplus.course.presentation.response.SectionUpsertResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final CourseQueryUseCase courseQueryUseCase;

    @PostMapping("/api/instructor/courses")
    public ResponseEntity<ResultResponse<CourseUpsertResponse>> createCourse(
            @RequestParam Long instructorId,
            @Valid @RequestBody CourseCreateRequest request
    ) {
        CourseUpsertResponse courseUpsertResponse = courseCommandUseCase.createCourse(instructorId, request.toCommand());

        return ResponseEntity
                .status(CourseResultCode.COURSE_REGISTER_SUCCESS.getStatus())
                .body(ResultResponse.of(
                        CourseResultCode.COURSE_REGISTER_SUCCESS,
                        courseUpsertResponse));
    }

    @PatchMapping("/api/instructor/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseUpsertResponse>> updateCourse(
            @PathVariable Long courseId,
            @RequestParam Long instructorId,
            @RequestBody CourseUpdateRequest request
    ) {
        CourseUpsertResponse courseUpsertResponse = courseCommandUseCase.updateCourse(courseId, instructorId, request.toCommand());

        return ResponseEntity
                .status(CourseResultCode.COURSE_UPDATE_SUCCESS.getStatus())
                .body(ResultResponse.of(
                        CourseResultCode.COURSE_UPDATE_SUCCESS,
                        courseUpsertResponse));
    }

    @GetMapping("/api/instructor/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseDetailResponse>> getInstructorCourseDetail(
            @PathVariable Long courseId,
            @RequestParam Long instructorId
    ) {
        CourseDetailResponse courseDetailResponse = courseQueryUseCase.getInstructorCourseDetail(courseId, instructorId);

        return ResponseEntity
                .status(CourseResultCode.COURSE_READ_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_READ_SUCCESS, courseDetailResponse));
    }

    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseDetailResponse>> getPublishedCourseDetail(
            @PathVariable Long courseId,
            @RequestParam Long userId
    ) {
        CourseDetailResponse courseDetailResponse = courseQueryUseCase.getPublishedCourseDetail(courseId, userId);

        return ResponseEntity
                .status(CourseResultCode.COURSE_READ_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_READ_SUCCESS, courseDetailResponse));
    }

    @GetMapping("/api/instructor/courses")
    public ResponseEntity<ResultResponse<PageResponse<CourseResponse>>> getInstructorCourses(
            @RequestParam Long instructorId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResponse> result = courseQueryUseCase.getInstructorCourses(instructorId, pageable);

        return ResponseEntity
                .status(CourseResultCode.COURSE_LIST_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_LIST_SUCCESS, PageResponse.from(result)));
    }

    @GetMapping("/api/courses")
    public ResponseEntity<ResultResponse<PageResponse<CourseResponse>>> getPublishedCourses(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResponse> result = courseQueryUseCase.getPublishedCourses(pageable);

        return ResponseEntity
                .status(CourseResultCode.COURSE_LIST_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_LIST_SUCCESS, PageResponse.from(result)));
    }

    @PostMapping("/api/instructor/courses/{courseId}/sections")
    public ResponseEntity<ResultResponse<SectionUpsertResponse>> createSection(
            @PathVariable Long courseId,
            @RequestParam Long instructorId,
            @Valid @RequestBody SectionCreateRequest request
    ) {
        SectionUpsertResponse response = courseCommandUseCase.createSection(courseId, instructorId, request.toCommand());

        return ResponseEntity
                .status(SectionResultCode.SECTION_REGISTER_SUCCESS.getStatus())
                .body(ResultResponse.of(SectionResultCode.SECTION_REGISTER_SUCCESS, response));
    }

    @PatchMapping("/api/instructor/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<ResultResponse<SectionUpsertResponse>> updateSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestParam Long instructorId,
            @RequestBody SectionUpdateRequest request
    ) {
        SectionUpsertResponse response = courseCommandUseCase.updateSection(courseId, instructorId, sectionId, request.toCommand());

        return ResponseEntity
                .status(SectionResultCode.SECTION_UPDATE_SUCCESS.getStatus())
                .body(ResultResponse.of(SectionResultCode.SECTION_UPDATE_SUCCESS, response));
    }

    @DeleteMapping("/api/instructor/courses/{courseId}")
    public ResponseEntity<ResultResponse<Void>> deleteCourse(
            @PathVariable Long courseId,
            @RequestParam Long instructorId
    ) {
        courseCommandUseCase.deleteCourse(courseId, instructorId);

        return ResponseEntity
                .status(CourseResultCode.COURSE_DELETE_SUCCESS.getStatus())
                .body(ResultResponse.from(CourseResultCode.COURSE_DELETE_SUCCESS));
    }

    @DeleteMapping("/api/instructor/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<ResultResponse<Void>> deleteSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestParam Long instructorId
    ) {
        courseCommandUseCase.deleteSection(courseId, instructorId, sectionId);

        return ResponseEntity
                .status(SectionResultCode.SECTION_DELETE_SUCCESS.getStatus())
                .body(ResultResponse.from(SectionResultCode.SECTION_DELETE_SUCCESS));
    }
}

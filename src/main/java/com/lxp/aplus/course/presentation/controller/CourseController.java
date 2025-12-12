package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.PageResponse;
import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.CourseResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.CurrentUser;
import com.lxp.aplus.common.security.InstructorOnly;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.course.application.usecase.CourseCommandUseCase;
import com.lxp.aplus.course.application.usecase.CourseQueryUseCase;
import com.lxp.aplus.course.presentation.request.CourseCreateRequest;
import com.lxp.aplus.course.presentation.request.CourseUpdateRequest;
import com.lxp.aplus.course.presentation.response.CourseDetailResponse;
import com.lxp.aplus.course.presentation.response.CoursePublishResponse;
import com.lxp.aplus.course.presentation.response.CourseResponse;
import com.lxp.aplus.course.presentation.response.CourseUpsertResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseCommandUseCase courseCommandUseCase;
    private final CourseQueryUseCase courseQueryUseCase;

    @InstructorOnly
    @PostMapping(value = "/api/instructor/courses", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse<CourseUpsertResponse>> createCourse(
            @Authenticated Long instructorId,
            @RequestPart("request") @Valid CourseCreateRequest request,
            @RequestPart("thumbnail") MultipartFile thumbnail
    ) {
        CourseUpsertResponse courseUpsertResponse = courseCommandUseCase.createCourse(instructorId, request.toCommand(thumbnail));

        return ResponseEntity
                .status(CourseResultCode.COURSE_REGISTER_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_REGISTER_SUCCESS, courseUpsertResponse));
    }

    @InstructorOnly
    @PatchMapping("/api/instructor/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseUpsertResponse>> updateCourse(
            @Authenticated Long instructorId,
            @PathVariable("courseId") Long courseId,
            @RequestBody CourseUpdateRequest request
    ) {
        CourseUpsertResponse courseUpsertResponse = courseCommandUseCase.updateCourse(courseId, instructorId, request.toCommand());

        return ResponseEntity
                .status(CourseResultCode.COURSE_UPDATE_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_UPDATE_SUCCESS, courseUpsertResponse));
    }

    @InstructorOnly
    @GetMapping("/api/instructor/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseDetailResponse>> getInstructorCourseDetail(
            @Authenticated Long instructorId,
            @PathVariable("courseId") Long courseId
    ) {
        CourseDetailResponse courseDetailResponse = courseQueryUseCase.getInstructorCourseDetail(courseId, instructorId);

        return ResponseEntity
                .status(CourseResultCode.COURSE_READ_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_READ_SUCCESS, courseDetailResponse));
    }

    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseDetailResponse>> getPublishedCourseDetail(
            @CurrentUser UserInfo userInfo,
            @PathVariable("courseId") Long courseId
    ) {
        CourseDetailResponse courseDetailResponse = courseQueryUseCase.getPublishedCourseDetail(courseId, userInfo.id());

        return ResponseEntity
                .status(CourseResultCode.COURSE_READ_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_READ_SUCCESS, courseDetailResponse));
    }

    @InstructorOnly
    @GetMapping("/api/instructor/courses")
    public ResponseEntity<ResultResponse<PageResponse<CourseResponse>>> getInstructorCourses(
            @Authenticated Long instructorId,
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

    @InstructorOnly
    @DeleteMapping("/api/instructor/courses/{courseId}")
    public ResponseEntity<ResultResponse<Void>> deleteCourse(
            @Authenticated Long instructorId,
            @PathVariable("courseId") Long courseId
    ) {
        courseCommandUseCase.deleteCourse(courseId, instructorId);

        return ResponseEntity
                .status(CourseResultCode.COURSE_DELETE_SUCCESS.getStatus())
                .body(ResultResponse.from(CourseResultCode.COURSE_DELETE_SUCCESS));
    }

    @InstructorOnly
    @PatchMapping("/api/instructor/courses/{courseId}/publish")
    public ResponseEntity<ResultResponse<CoursePublishResponse>> publishCourse(
            @PathVariable Long courseId,
            @Authenticated Long instructorId
    ) {
        CoursePublishResponse response = courseCommandUseCase.publishCourse(courseId, instructorId);

        return ResponseEntity
                .status(CourseResultCode.COURSE_PUBLISH_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_PUBLISH_SUCCESS, response));
    }
}

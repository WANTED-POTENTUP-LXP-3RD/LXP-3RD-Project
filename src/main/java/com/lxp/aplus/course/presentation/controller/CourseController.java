package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.PageResponse;
import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.CourseResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.CurrentUser;
import com.lxp.aplus.common.security.InstructorOnly;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.course.application.result.CourseDetailResult;
import com.lxp.aplus.course.application.result.CoursePublishResult;
import com.lxp.aplus.course.application.result.CourseResult;
import com.lxp.aplus.course.application.result.CourseUpsertResult;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lxp.aplus.course.domain.CourseLevel;

@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseCommandUseCase courseCommandUseCase;
    private final CourseQueryUseCase courseQueryUseCase;

    @InstructorOnly
    @PostMapping(value = "/api/instructor/courses")
    public ResponseEntity<ResultResponse<CourseUpsertResponse>> createCourse(
            @Authenticated Long instructorId,
            @RequestBody @Valid CourseCreateRequest request
    ) {
        CourseUpsertResult result = courseCommandUseCase.createCourse(instructorId, request.toCommand());

        return ResponseEntity
                .status(CourseResultCode.COURSE_REGISTER_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_REGISTER_SUCCESS, CourseUpsertResponse.from(result)));
    }

    @InstructorOnly
    @PatchMapping("/api/instructor/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseUpsertResponse>> updateCourse(
            @Authenticated Long instructorId,
            @PathVariable("courseId") Long courseId,
            @RequestBody CourseUpdateRequest request
    ) {
        CourseUpsertResult result = courseCommandUseCase.updateCourse(courseId, instructorId, request.toCommand());

        return ResponseEntity
                .status(CourseResultCode.COURSE_UPDATE_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_UPDATE_SUCCESS, CourseUpsertResponse.from(result)));
    }

    @InstructorOnly
    @GetMapping("/api/instructor/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseDetailResponse>> getInstructorCourseDetail(
            @Authenticated Long instructorId,
            @PathVariable("courseId") Long courseId
    ) {
        CourseDetailResult result = courseQueryUseCase.getInstructorCourseDetail(courseId, instructorId);

        return ResponseEntity
                .status(CourseResultCode.COURSE_READ_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_READ_SUCCESS, CourseDetailResponse.from(result)));
    }

    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<ResultResponse<CourseDetailResponse>> getPublishedCourseDetail(
            @CurrentUser UserInfo userInfo,
            @PathVariable("courseId") Long courseId
    ) {
        Long userId = (userInfo != null) ? userInfo.id() : null;
        CourseDetailResult result = courseQueryUseCase.getPublishedCourseDetail(courseId, userId);

        return ResponseEntity
                .status(CourseResultCode.COURSE_READ_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_READ_SUCCESS, CourseDetailResponse.from(result)));
    }

    @InstructorOnly
    @GetMapping("/api/instructor/courses")
    public ResponseEntity<ResultResponse<PageResponse<CourseResponse>>> getInstructorCourses(
            @Authenticated Long instructorId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResult> result = courseQueryUseCase.getInstructorCourses(instructorId, pageable);
        Page<CourseResponse> responsePage = result.map(CourseResponse::from);

        return ResponseEntity
                .status(CourseResultCode.COURSE_LIST_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_LIST_SUCCESS, PageResponse.from(responsePage)));
    }

    @GetMapping("/api/courses")
    public ResponseEntity<ResultResponse<PageResponse<CourseResponse>>> getPublishedCourses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CourseLevel level,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResult> result = courseQueryUseCase.getPublishedCourses(title, categoryId, level, pageable);
        Page<CourseResponse> responsePage = result.map(CourseResponse::from);

        return ResponseEntity
                .status(CourseResultCode.COURSE_LIST_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_LIST_SUCCESS, PageResponse.from(responsePage)));
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
        CoursePublishResult result = courseCommandUseCase.publishCourse(courseId, instructorId);

        return ResponseEntity
                .status(CourseResultCode.COURSE_PUBLISH_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_PUBLISH_SUCCESS, CoursePublishResponse.from(result)));
    }
}

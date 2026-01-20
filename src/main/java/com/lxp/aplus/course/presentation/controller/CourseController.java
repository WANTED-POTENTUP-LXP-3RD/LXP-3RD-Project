package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.result.PageResponse;
import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.CourseResultCode;
import com.lxp.aplus.common.security.CurrentUser;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.course.application.result.CourseDetailResult;
import com.lxp.aplus.course.application.result.CourseResult;
import com.lxp.aplus.course.application.usecase.CourseCommandUseCase;
import com.lxp.aplus.course.application.usecase.CourseQueryUseCase;
import com.lxp.aplus.course.presentation.response.CourseDetailResponse;
import com.lxp.aplus.course.presentation.response.CourseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseCommandUseCase courseCommandUseCase;
    private final CourseQueryUseCase courseQueryUseCase;

    @GetMapping("/{courseId}")
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

    @GetMapping
    public ResponseEntity<ResultResponse<PageResponse<CourseResponse>>> getPublishedCourses(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResult> result = courseQueryUseCase.getPublishedCourses(pageable);
        Page<CourseResponse> responsePage = result.map(CourseResponse::from);

        return ResponseEntity
                .status(CourseResultCode.COURSE_LIST_SUCCESS.getStatus())
                .body(ResultResponse.of(CourseResultCode.COURSE_LIST_SUCCESS, PageResponse.from(responsePage)));
    }
}

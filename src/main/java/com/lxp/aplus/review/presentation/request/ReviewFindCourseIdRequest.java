package com.lxp.aplus.review.presentation.request;

import java.util.List;

public record ReviewFindCourseIdRequest(
        List<Long> courseIds
) {
}

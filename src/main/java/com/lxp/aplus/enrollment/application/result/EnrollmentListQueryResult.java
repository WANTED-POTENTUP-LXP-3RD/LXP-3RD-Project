package com.lxp.aplus.enrollment.application.result;

import org.springframework.data.domain.Page;

import java.util.List;

public record EnrollmentListQueryResult(
        List<EnrollmentListItemResult> content,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) {
    public static EnrollmentListQueryResult of(Page<?> page, List<EnrollmentListItemResult> content) {
        return new EnrollmentListQueryResult(
                content,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }
}

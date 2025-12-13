package com.lxp.aplus.category.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lxp.aplus.category.application.result.CategoryResult;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CategoryResponse(
        Long categoryId,
        String name,
        List<CategoryResponse> children
) {
    public static CategoryResponse from(CategoryResult result) {
        return CategoryResponse.builder()
                .categoryId(result.categoryId())
                .name(result.name())
                .children(result.children().stream()
                        .map(CategoryResponse::from)
                        .toList())
                .build();
    }

    public static List<CategoryResponse> from(List<CategoryResult> results) {
        return results.stream()
                .map(CategoryResponse::from)
                .toList();
    }
}

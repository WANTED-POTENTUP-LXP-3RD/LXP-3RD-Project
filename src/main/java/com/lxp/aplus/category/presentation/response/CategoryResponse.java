package com.lxp.aplus.category.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lxp.aplus.category.domain.Category;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CategoryResponse(
        Long categoryId,
        String name,
        List<CategoryResponse> children
) {
    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .children(category.getChildren() != null ?
                        category.getChildren().stream()
                                .map(CategoryResponse::from)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .build();
    }

    public static List<CategoryResponse> from(List<Category> categories) {
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
}

package com.lxp.aplus.category.application.dto.response;

import com.lxp.aplus.category.domain.Category;

import java.util.List;

public record CategoryResult(
        Long categoryId,
        String name,
        List<CategoryResult> children
) {
    public static CategoryResult from(Category category) {
        return new CategoryResult(
                category.getId(),
                category.getName(),
                category.getChildren().stream()
                        .map(CategoryResult::from)
                        .toList()
        );
    }
}

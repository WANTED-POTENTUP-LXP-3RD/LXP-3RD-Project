package com.lxp.aplus.category.application.internal.dto;

import com.lxp.aplus.category.domain.Category;
import lombok.Builder;

import java.util.List;

@Builder
public record CategoryInternalResult(
        Long id,
        String name,
        Category parent,
        List<Category> children
) {
        public static CategoryInternalResult from(Category category) {

            return CategoryInternalResult.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .parent(category.getParent())
                    .children(category.getChildren())
                    .build();
        }
}

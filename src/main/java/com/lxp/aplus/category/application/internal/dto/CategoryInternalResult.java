package com.lxp.aplus.category.application.internal.dto;

import com.lxp.aplus.category.domain.Category;
import lombok.Builder;

import java.util.List;

@Builder
public record CategoryInternalDto(
        Long id,
        String name,
        Category parent,
        List<Category> children
) {
        public static CategoryInternalDto from(Category category) {

            return CategoryInternalDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .parent(category.getParent())
                    .children(category.getChildren())
                    .build();
        }
}

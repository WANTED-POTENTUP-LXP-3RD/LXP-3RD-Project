package com.lxp.aplus.category.application.internal.dto;

import com.lxp.aplus.category.domain.Category;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record CategoryInternalResult(
        Long id,
        String name,
        String parentName,
        List<Long> childrenIds
) {
        public static CategoryInternalResult from(Category category) {
            String parentName = category.getParent() != null ? category.getParent().getName() : null;
            List<Long> childrenIds = category.getChildren().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());

            return CategoryInternalResult.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .parentName(parentName)
                    .childrenIds(childrenIds)
                    .build();
        }
}

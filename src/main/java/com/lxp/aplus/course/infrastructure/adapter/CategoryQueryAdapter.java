package com.lxp.aplus.course.infrastructure.adapter;

import com.lxp.aplus.category.application.port.out.CategoryRepository;
import com.lxp.aplus.category.domain.Category;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CategoryErrorCode;
import com.lxp.aplus.course.application.port.out.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryQueryAdapter implements CategoryQueryPort {
    private final CategoryRepository categoryRepository;

    @Override
    public List<String> findCategoryWithParentNames(Long categoryId) {
        Category category = categoryRepository.findByIdWithParent(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        List<String> categoryNames = new ArrayList<>();
        categoryNames.add(category.getName());

        if (category.getParent() != null) {
            categoryNames.add(0, category.getParent().getName());
        }

        return categoryNames;
    }

    @Override
    public Map<Long, List<String>> getCategoryNamesBatch(List<Long> categoryIds) {
        return categoryRepository.findAllByIdIn(categoryIds)
                .stream()
                .collect(Collectors.toMap(
                        Category::getId,
                        this::extractCategoryNameList
                ));
    }

    private List<String> extractCategoryNameList(Category category) {
        if (category.getParent() != null) {
            return List.of(
                    category.getParent().getName(),
                    category.getName()
            );
        }
        return List.of(category.getName());
    }

    @Override
    public List<Long> getCategoryIds(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        List<Long> ids = new ArrayList<>();
        ids.add(category.getId());

        // 부모가 없으면 대분류이므로 자식(소분류)들을 모두 추가
        if (category.getParent() == null) {
            category.getChildren().forEach(child -> ids.add(child.getId()));
        }

        return ids;
    }
}

package com.lxp.aplus.course.infrastructure.adapter;

import com.lxp.aplus.category.domain.Category;
import com.lxp.aplus.category.domain.CategoryRepository;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CategoryErrorCode;
import com.lxp.aplus.course.application.port.out.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
<<<<<<<< HEAD:src/main/java/com/lxp/aplus/course/infrastructure/adapter/CategoryQueryAdapter.java
public class CategoryQueryAdapter implements CategoryQueryPort {
========
public class CategoryFinderAdapter implements CategoryQueryPort {
>>>>>>>> 3a8706dbc987e6998bb9642886f2c03272c67e72:src/main/java/com/lxp/aplus/course/infrastructure/adapter/CategoryFinderAdapter.java
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
}

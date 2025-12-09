package com.lxp.aplus.course.infrastructure;

import com.lxp.aplus.category.domain.Category;
import com.lxp.aplus.category.domain.CategoryRepository;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CategoryErrorCode;
import com.lxp.aplus.course.application.port.out.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryQueryPort {
    private final CategoryRepository categoryRepository;

    @Override
    public List<String> findCategoryPathIds(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        List<String> path = new ArrayList<>();

        while (category != null) {
            path.add(String.valueOf(category.getName()));
            category = category.getParent();
        }

        Collections.reverse(path);
        return path;
    }
}

package com.lxp.aplus.category.application.usecase;

import com.lxp.aplus.category.application.result.CategoryResult;
import com.lxp.aplus.category.domain.Category;
import com.lxp.aplus.category.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryUseCase {
    private final CategoryRepository categoryRepository;

    public List<CategoryResult> getAllCategories() {
        List<Category> categories = categoryRepository.findAllRootWithChildren();
        return categories.stream()
                .map(CategoryResult::from)
                .toList();
    }
}

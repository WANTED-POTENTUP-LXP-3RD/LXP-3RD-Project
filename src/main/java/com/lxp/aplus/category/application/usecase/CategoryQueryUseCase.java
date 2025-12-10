package com.lxp.aplus.category.application.usecase;

import com.lxp.aplus.category.domain.Category;
import com.lxp.aplus.category.domain.CategoryRepository;
import com.lxp.aplus.category.presentation.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryUseCase {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        List<Category> categoryResults = categoryRepository.findAllRootWithChildren();
        return CategoryResponse.from(categoryResults);
    }
}

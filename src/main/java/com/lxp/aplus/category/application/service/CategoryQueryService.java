package com.lxp.aplus.category.application.service;

import com.lxp.aplus.category.application.dto.response.CategoryResult;
import com.lxp.aplus.category.application.internal.dto.CategoryInternalResult;
import com.lxp.aplus.category.application.port.in.CategoryQueryUseCase;
import com.lxp.aplus.category.domain.Category;
import com.lxp.aplus.category.application.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService implements CategoryQueryUseCase {
    private final CategoryRepository categoryRepository;

    public List<CategoryResult> getAllCategories() {
        List<Category> categories = categoryRepository.findAllRootWithChildren();
        return categories.stream()
                .map(CategoryResult::from)
                .toList();
    }

    @Override
    public Optional<CategoryInternalResult> findByIdWithParent(Long id) {
        return categoryRepository.findByIdWithParent(id)
                .map(CategoryInternalResult::from);
    }
}

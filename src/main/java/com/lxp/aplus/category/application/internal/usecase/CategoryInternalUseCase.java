package com.lxp.aplus.category.application.internal.usecase;

import com.lxp.aplus.category.application.internal.dto.CategoryInternalResult;
import com.lxp.aplus.category.application.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryInternalUseCase {
    private final CategoryRepository categoryRepository;

    public Optional<CategoryInternalResult> findByIdWithParent(Long id){
        return categoryRepository.findByIdWithParent(id)
                .map(CategoryInternalResult::from);
    }
}

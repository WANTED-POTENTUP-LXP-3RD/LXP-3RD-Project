package com.lxp.aplus.category.application.internal.usecase;

import com.lxp.aplus.category.application.internal.dto.CategoryInternalDto;
import com.lxp.aplus.category.application.port.out.CategoryRepository;
import com.lxp.aplus.category.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryInternalUseCase {
    private final CategoryRepository categoryRepository;

    public Optional<CategoryInternalDto> findByIdWithParent(Long id){
        return categoryRepository.findByIdWithParent(id)
                .map(CategoryInternalDto::from);
    }
}

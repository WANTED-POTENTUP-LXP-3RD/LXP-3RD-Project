package com.lxp.aplus.category.application.port.in;

import com.lxp.aplus.category.application.dto.response.CategoryResult;
import com.lxp.aplus.category.application.internal.dto.CategoryInternalDto;

import java.util.List;
import java.util.Optional;

public interface CategoryQueryUseCase {

    List<CategoryResult> getAllCategories();

    Optional<CategoryInternalDto> findByIdWithParent(Long id);
}

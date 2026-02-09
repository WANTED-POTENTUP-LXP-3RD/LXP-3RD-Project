package com.lxp.aplus.category.application.port.in;

import com.lxp.aplus.category.application.dto.result.CategoryResult;
import com.lxp.aplus.category.application.internal.dto.CategoryInternalResult;

import java.util.List;
import java.util.Optional;

public interface CategoryQueryUseCase {

    List<CategoryResult> getAllCategories();

    Optional<CategoryInternalResult> findByIdWithParent(Long id);
}

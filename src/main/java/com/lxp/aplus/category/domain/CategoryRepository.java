package com.lxp.aplus.category.domain;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findById(Long id);
    List<Category> findAllRootWithChildren();
    Optional<Category> findByIdWithParent(Long id);
    List<Category> findAllByIdIn(List<Long> ids);
}

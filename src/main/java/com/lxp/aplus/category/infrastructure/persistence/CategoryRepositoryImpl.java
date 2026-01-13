package com.lxp.aplus.category.infrastructure.persistence;

import com.lxp.aplus.category.domain.Category;
import com.lxp.aplus.category.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    private final CategoryJpaRepository jpaRepository;

    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Category> findAllRootWithChildren() {
        return jpaRepository.findAllRootWithChildren();
    }

    @Override
    public Optional<Category> findByIdWithParent(Long id) {
        return jpaRepository.findByIdWithParent(id);
    }

    @Override
    public List<Category> findAllByIdIn(List<Long> ids) {
        return jpaRepository.findAllByIdIn(ids);
    }
}

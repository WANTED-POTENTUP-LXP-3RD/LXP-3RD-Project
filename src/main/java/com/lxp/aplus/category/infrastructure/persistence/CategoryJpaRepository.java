package com.lxp.aplus.category.infrastructure.persistence;

import com.lxp.aplus.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
}

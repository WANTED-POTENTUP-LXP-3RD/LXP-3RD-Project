package com.lxp.aplus.course.application.port.out;

import java.util.List;

public interface CategoryQueryPort {
    List<String> findCategoryWithParentNames(Long categoryId);
}

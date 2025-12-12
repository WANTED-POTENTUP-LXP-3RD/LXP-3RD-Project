package com.lxp.aplus.course.application.port.out;

import java.util.List;
import java.util.Map;

public interface CategoryQueryPort {
    List<String> findCategoryWithParentNames(Long categoryId);
    Map<Long, List<String>> getCategoryNamesBatch(List<Long> categoryIds);

}

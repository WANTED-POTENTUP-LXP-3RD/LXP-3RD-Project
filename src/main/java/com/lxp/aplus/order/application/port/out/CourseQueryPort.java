package com.lxp.aplus.order.application.port.out;

import java.util.List;
import java.util.Map;

public interface CourseQueryPort {

    /*
     * course 가격 조회
     */
    Map<Long, Integer> getCoursePriceByIds(List<Long> courseIds);
}

package com.lxp.aplus.order.application.port.out;

import java.util.List;
import java.util.Map;

public interface CourseQueryPort {

    /**
     * 특정 강좌가 PUBLISHED 상태인지 확인한다.
     */
    boolean isCoursePublished(Long courseId);

    /*
     * course별 판매 상태(가격, 상태) 조회
     */
    Map<Long, CourseSalesStatus> getCourseSalesStatusByIds(List<Long> courseIds);
}

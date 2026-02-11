package com.lxp.aplus.order.application.port.out;

import com.lxp.aplus.order.application.port.out.dto.CoursePrice;
import com.lxp.aplus.order.application.port.out.dto.CourseSnapshot;
import com.lxp.aplus.order.application.port.out.dto.CourseSalesStatus;

import java.util.List;
import java.util.Map;

public interface CourseQueryPort {

    /*
     * 강좌 판매 가능 여부 조회
     */
    boolean isCourseAvailableForSale(Long courseId);

    /*
     * 강좌 판매 상태(가격, 상태) 조회
     */
    Map<Long, CourseSalesStatus> getCourseSalesStatusByIds(List<Long> courseIds);

    /*
     * 강좌의 상품 정보 조회
     */
    Map<Long, CourseSnapshot> getCourseSnapshot(List<Long> courseIds);

    /*
     * 강좌 가격 조회
     */
    List<CoursePrice> getCoursePrices(List<Long> courseIds);
}

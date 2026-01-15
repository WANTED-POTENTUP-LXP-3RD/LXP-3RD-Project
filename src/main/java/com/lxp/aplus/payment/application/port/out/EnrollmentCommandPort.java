package com.lxp.aplus.payment.application.port.out;

import java.util.Map;

public interface EnrollmentCommandPort {

    /*
     * 특정 사용자에게 구매 강좌에 대한 수강 권한을 동기적으로 부여한다.
     * TODO: 나중에 이벤트로 비동기 처리하도록 리팩터
     * @param userId 수강을 요청하는 사용자 ID
     * @param courseToOrderItemMap { courseId: orderItemId }
     */
    void enrollUserInCourses(Long userId, Map<Long, Long> courseToOrderItemMaps);
}

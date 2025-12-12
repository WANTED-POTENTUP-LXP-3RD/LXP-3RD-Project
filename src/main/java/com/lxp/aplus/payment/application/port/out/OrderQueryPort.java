package com.lxp.aplus.payment.application.port.out;

import java.util.List;

public interface OrderQueryPort {

    /*
     * OrderLine ID(= courseId) 조회
     */
    List<Long> getCourseIdsByOrderId(String orderId);
}

package com.lxp.aplus.review.application.port.out;

import java.util.List;
import java.util.Map;

public interface UserQueryPort {
    public Map<Long, String> findUserNames(List<Long> userIds);
}

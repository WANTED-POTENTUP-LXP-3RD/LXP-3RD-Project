package com.lxp.aplus.review.infrastructure.adapter;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.UserErrorCode;
import com.lxp.aplus.review.application.port.out.UserQueryPort;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class ReviewUserQueryAdapter implements UserQueryPort {
    private final UserRepository userRepository;

    @Override
    public Map<Long, String> findUserNames(List<Long> userIds) {
        return userRepository.findByIdIn(userIds).stream().collect(Collectors.toMap(User::getId, User::getNickName));
    }

}

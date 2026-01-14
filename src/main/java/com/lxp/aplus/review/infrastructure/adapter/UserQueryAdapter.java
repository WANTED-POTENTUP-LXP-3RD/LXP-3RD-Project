package com.lxp.aplus.review.infrastructure.adapter;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.UserErrorCode;
import com.lxp.aplus.review.application.port.out.UserQueryPort;
import com.lxp.aplus.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryPort {
    private final UserRepository userRepository;

    @Override
    public String findUserName(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND)).getNickName();
    }
}

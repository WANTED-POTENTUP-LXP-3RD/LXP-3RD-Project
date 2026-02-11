package com.lxp.aplus.user.application.internal.usecase;

import com.lxp.aplus.user.application.internal.dto.UserInternalResult;
import com.lxp.aplus.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInternalUseCase {
    private final UserRepository userRepository;

    public Optional<UserInternalResult> findById(Long id) {
        return userRepository.findById(id)
                .map(UserInternalResult::from);
    }
}

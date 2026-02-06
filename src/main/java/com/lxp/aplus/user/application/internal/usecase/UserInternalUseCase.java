package com.lxp.aplus.user.application.internal.usecase;

import com.lxp.aplus.user.application.internal.dto.UserInternalDto;
import com.lxp.aplus.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInternalUseCase {
    private final UserRepository userRepository;

    public Optional<UserInternalDto> findById(Long id) {
        return userRepository.findById(id)
                .map(UserInternalDto::from);
    }
}

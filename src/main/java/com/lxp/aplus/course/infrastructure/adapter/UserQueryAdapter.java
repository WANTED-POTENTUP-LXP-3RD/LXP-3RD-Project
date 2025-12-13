package com.lxp.aplus.course.infrastructure.adapter;

import com.lxp.aplus.course.application.port.out.UserQueryPort;
import com.lxp.aplus.course.application.result.InstructorResult;
import com.lxp.aplus.course.presentation.response.InstructorResponse;
import com.lxp.aplus.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryPort {
    private final UserRepository userRepository;

    @Override
    public Optional<InstructorResult> findInstructorById(Long userId) {
        return userRepository.findById(userId)
                .map(InstructorResult::from);
    }
}

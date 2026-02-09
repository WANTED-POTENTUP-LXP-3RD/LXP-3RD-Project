package com.lxp.aplus.review.infrastructure.adapter.module;

import com.lxp.aplus.review.application.port.out.UserQueryPort;
import com.lxp.aplus.user.application.internal.dto.UserInternalResult;
import com.lxp.aplus.user.application.internal.usecase.UserInternalUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewUserModuleAdapter implements UserQueryPort {
    private final UserInternalUseCase userInternalUseCase;

    @Override
    public Map<Long, String> findUserNames(List<Long> userIds) {
        return userIds.stream()
                .map(userInternalUseCase::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(UserInternalResult::id, UserInternalResult::nickName));
    }
}

package com.lxp.aplus.common.security;

import com.lxp.aplus.user.domain.Role;
import com.lxp.aplus.user.domain.RoleType;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.Optional;

/**
 * @CurrentUser 어노테이션이 붙은 파라미터에 현재 로그인한 사용자 정보를 주입하는 Resolver
 * 
 * 인증되지 않은 사용자가 접근하면 null을 반환합니다. (예외 발생하지 않음)
 * 선택적 인증이 필요한 경우에 사용합니다.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
               parameter.getParameterType().equals(UserInfo.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        
        Long userId = SecurityUtils.getCurrentUserId();
        
        // 사용자가 없으면 null 반환 (예외 발생하지 않음)
        if (userId == null) {
            return null;
        }

        // 사용자와 역할 정보 조회
        Optional<User> user = userRepository.findUserWithRolesById(userId);
        
        // 사용자를 찾을 수 없으면 null 반환
        if (user.isEmpty()) {
            return null;
        }

        // Role 목록 추출 (삭제되지 않은 Role만)
        List<RoleType> roles = user.get().getRoles().stream()
                .filter(role -> role.getDeletedAt() == null)
                .map(Role::getRoleType)
                .toList();

        return new UserInfo(userId, roles);
    }
}


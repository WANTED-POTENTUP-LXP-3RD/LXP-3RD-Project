package com.lxp.aplus.common.security;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import com.lxp.aplus.user.application.dto.AuthUser;
import com.lxp.aplus.user.application.port.in.UserQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @Authenticated 어노테이션이 붙은 파라미터에 현재 로그인한 사용자 정보를 주입하는 Resolver
 * 
 * 지원하는 타입:
 * - Long: 사용자 ID만 반환
 * - UserInfo: 사용자 ID와 역할 목록 반환
 * 
 * 인증되지 않은 사용자가 접근하면 UNAUTHORIZED 예외를 발생시킵니다.
 */
@Component
@RequiredArgsConstructor
public class AuthenticatedArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserQueryUseCase userQueryUseCase;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(Authenticated.class)) {
            return false;
        }
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.equals(Long.class) || parameterType.equals(UserInfo.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        
        Long userId = SecurityUtils.getCurrentUserId();
        
        if (userId == null) {
            throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
        }
        
        // Long 타입이면 userId만 반환
        if (parameter.getParameterType().equals(Long.class)) {
            return userId;
        }
        
        // UserInfo 타입이면 사용자 정보 조회하여 반환
        AuthUser authUser = userQueryUseCase.findUserForAuthById(userId)
                .orElseThrow(() -> new BusinessException(com.lxp.aplus.common.error.code.UserErrorCode.USER_NOT_FOUND));
        
        return new UserInfo(authUser.id(), authUser.roles());
    }
}


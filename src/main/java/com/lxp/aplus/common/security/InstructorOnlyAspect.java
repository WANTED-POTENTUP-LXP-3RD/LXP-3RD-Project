package com.lxp.aplus.common.security;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import com.lxp.aplus.common.error.code.UserErrorCode;
import com.lxp.aplus.user.domain.RoleType;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @InstructorOnly 어노테이션을 처리하는 AOP Aspect
 * <p>
 * 메서드 실행 전에 강사 권한을 체크합니다.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class InstructorOnlyAspect {

    private final UserRepository userRepository;

    @Before("@annotation(com.lxp.aplus.common.security.InstructorOnly)")
    public void checkInstructorRole(JoinPoint joinPoint) {
        // 메서드 파라미터에서 @Authenticated로 주입된 userId 찾기
        Object[] args = joinPoint.getArgs();
        Long userId = findUserId(joinPoint);

        if (userId == null) {
            log.warn("@InstructorOnly 메서드에 @Authenticated 파라미터가 없습니다: {}", joinPoint.getSignature());
            throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
        }

        // 사용자 조회 및 강사 권한 체크
        User user = userRepository.findUserWithRolesById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        boolean hasInstructorRole = user.getRoles().stream()
                .anyMatch(role -> role.getRoleType() == RoleType.INSTRUCTOR && role.getDeletedAt() == null);

        if (!hasInstructorRole) {
            log.warn("강사 권한이 없는 사용자가 강사 전용 메서드에 접근 시도: userId={}, method={}",
                    userId, joinPoint.getSignature());
            throw new BusinessException(UserErrorCode.NOT_INSTRUCTOR);
        }
    }

    /**
     * 메서드 파라미터에서 Long 타입의 userId를 찾습니다.
     *
     * @param args 메서드 파라미터 배열
     * @return userId (없으면 null)
     */
    private Long findUserId(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < args.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Authenticated) {
                    Object arg = args[i]; // 1. Long 타입인 경우 (userId)
                    if (arg instanceof Long) {
                        return (Long) arg;
                    } // 2. UserInfo 타입인 경우(userInfo.id())
                    if (arg instanceof UserInfo) {
                        return ((UserInfo) arg).id();
                    }
                }
            }
        }
        return null;
    }
}


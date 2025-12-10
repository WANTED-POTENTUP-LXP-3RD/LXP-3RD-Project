package com.lxp.aplus.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 로그인한 사용자 ID를 메서드 파라미터로 주입받는 어노테이션
 * 
 * 이 어노테이션이 붙은 파라미터는 인증된 사용자의 ID가 자동으로 주입됩니다.
 * 인증되지 않은 경우 UNAUTHORIZED 예외가 발생합니다.
 * 
 * 사용 예시:
 * <pre>
 * {@code
 * @GetMapping("/me")
 * public ResponseEntity<UserResponse> getMyInfo(@Authenticated Long userId) {
 *     // userId는 자동으로 주입됨 (인증되지 않은 경우 예외 발생)
 *     return userService.getUser(userId);
 * }
 * }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {
}


package com.lxp.aplus.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 관리자 권한이 필요한 메서드를 표시하는 어노테이션
 *
 * 이 어노테이션이 붙은 메서드는 관리자 권한을 가진 사용자만 접근할 수 있습니다.
 * 관리자 권한이 없는 경우 NOT_ADMIN 예외가 발생합니다.
 *
 * 사용 예시:
 * <pre>
 * {@code
 * @AdminOnly
 * @PostMapping("/admin/users")
 * public ResponseEntity<UserResponse> manageUsers(@Authenticated Long userId, ...) {
 *     // 관리자만 접근 가능
 *     return ...;
 * }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminOnly {
}

package com.lxp.aplus.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 로그인한 사용자 정보를 메서드 파라미터로 주입받는 어노테이션
 * 
 * 이 어노테이션이 붙은 파라미터는 인증된 사용자의 정보가 자동으로 주입됩니다.
 * 인증되지 않은 경우 null이 반환됩니다. (예외 발생하지 않음)
 * 
 * 선택적 인증이 필요한 경우에 사용합니다.
 * 필수 인증이 필요한 경우에는 @Authenticated를 사용하세요.
 * 
 * 사용 예시:
 * <pre>
 * {@code
 * @GetMapping("/courses")
 * public ResponseEntity<CourseResponse> getCourses(@CurrentUser UserInfo userInfo) {
 *     if (userInfo == null) {
 *         // 비로그인 사용자 처리
 *         return ResponseEntity.ok(getPublicCourses());
 *     }
 *     // 로그인 사용자 처리
 *     Long userId = userInfo.id();
 *     List<RoleType> roles = userInfo.roles();
 *     return ResponseEntity.ok(getUserCourses(userId));
 * }
 * }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}


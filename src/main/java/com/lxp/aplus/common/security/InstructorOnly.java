package com.lxp.aplus.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 강사 권한이 필요한 메서드를 표시하는 어노테이션
 * 
 * 이 어노테이션이 붙은 메서드는 강사 권한을 가진 사용자만 접근할 수 있습니다.
 * 강사 권한이 없는 경우 NOT_INSTRUCTOR 예외가 발생합니다.
 * 
 * 사용 예시:
 * <pre>
 * {@code
 * @InstructorOnly
 * @PostMapping("/courses")
 * public ResponseEntity<CourseResponse> createCourse(@Authenticated Long userId, ...) {
 *     // 강사만 접근 가능
 *     return ...;
 * }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InstructorOnly {
}


package com.lxp.aplus.common.security;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.UserErrorCode;
import com.lxp.aplus.user.domain.RoleType;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 사용자 권한 체크를 담당
 */
@Component
@RequiredArgsConstructor
public class RoleChecker {

    private final UserRepository userRepository;

    /**
     * 현재 사용자가 강사 권한을 가지고 있는지 확인
     * 
     * @param userId 사용자 ID
     * @throws BusinessException 강사 권한이 없는 경우 NOT_INSTRUCTOR 예외 발생
     */
    public void requireInstructor(Long userId) {
        User user = userRepository.findUserWithRolesById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        boolean hasInstructorRole = user.getRoles().stream()
                .anyMatch(role -> role.getRoleType() == RoleType.INSTRUCTOR && role.getDeletedAt() == null);

        if (!hasInstructorRole) {
            throw new BusinessException(UserErrorCode.NOT_INSTRUCTOR);
        }
    }

    /**
     * 현재 사용자가 관리자 권한을 가지고 있는지 확인
     * 
     * @param userId 사용자 ID
     * @throws BusinessException 관리자 권한이 없는 경우 예외 발생 (추후 구현)
     */
    public void requireAdmin(Long userId) {
        User user = userRepository.findUserWithRolesById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        boolean hasAdminRole = user.getRoles().stream()
                .anyMatch(role -> role.getRoleType() == RoleType.ADMIN && role.getDeletedAt() == null);

        if (!hasAdminRole) {
            // TODO: NOT_ADMIN 에러 코드 추가 필요
            throw new BusinessException(UserErrorCode.NOT_INSTRUCTOR); // 임시
        }
    }
}


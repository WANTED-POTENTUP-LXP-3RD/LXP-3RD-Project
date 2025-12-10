package com.lxp.aplus.common.security;

import com.lxp.aplus.user.domain.RoleType;

import java.util.List;

/**
 * 현재 로그인한 사용자의 기본 정보를 담는 DTO
 * 
 * 다른 도메인에서도 사용할 수 있도록 common 패키지에 위치합니다.
 */
public record UserInfo(
        Long id,
        List<RoleType> roles
) {
    /**
     * 사용자가 특정 역할을 가지고 있는지 확인
     * 
     * @param roleType 확인할 역할 타입
     * @return 역할 보유 여부
     */
    public boolean hasRole(RoleType roleType) {
        return roles.contains(roleType);
    }

    /**
     * 사용자가 강사 권한을 가지고 있는지 확인
     * 
     * @return 강사 권한 보유 여부
     */
    public boolean isInstructor() {
        return hasRole(RoleType.INSTRUCTOR);
    }

    /**
     * 사용자가 관리자 권한을 가지고 있는지 확인
     * 
     * @return 관리자 권한 보유 여부
     */
    public boolean isAdmin() {
        return hasRole(RoleType.ADMIN);
    }
}


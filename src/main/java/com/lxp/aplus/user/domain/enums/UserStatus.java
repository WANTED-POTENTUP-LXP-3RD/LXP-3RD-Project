package com.lxp.aplus.user.domain.enums;

/**
 * ACTIVE	정상 회원
 * INACTIVE	휴면 회원
 * WITHDRAWN	탈퇴 회원
 * PENDING	가입대기(이메일 인증 전)	DEFAULT VALUE
 * SUSPENDED	일시정지	Unused
 * BANNED	영구정지	Unused
 * BLOCKED	관리자에의한 서비스 접근제한	Unused
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    WITHDRAWN,
    PENDING,
    SUSPENDED,
    BANNED,
    BLOCKED;
}

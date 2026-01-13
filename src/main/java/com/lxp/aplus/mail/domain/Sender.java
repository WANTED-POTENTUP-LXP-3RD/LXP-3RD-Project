package com.lxp.aplus.mail.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * 메일 발신자 Value Object
 * 내부인(시스템)과 외부인(외부 발신자) 모두 지원
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Sender {

    /**
     * 발신자 이메일 주소 (필수)
     */
    private final String email;

    /**
     * 발신자 User ID (선택적, 내부인인 경우에만 존재)
     * 예: 특정 사용자가 다른 사용자에게 메일을 보내는 경우
     */
    private final Long userId;

    /**
     * 내부인 여부
     * userId가 있으면 내부인, 없으면 외부인(시스템 발신자)
     */
    public boolean isInternal() {
        return userId != null;
    }

    /**
     * 외부인 여부 (시스템 발신자)
     */
    public boolean isExternal() {
        return !isInternal();
    }

    /**
     * User ID 조회 (Optional)
     */
    public Optional<Long> getUserId() {
        return Optional.ofNullable(userId);
    }

    /**
     * 내부인 발신자 생성
     * 특정 사용자가 다른 사용자에게 메일을 보내는 경우
     *
     * @param email 이메일 주소
     * @param userId User ID
     * @return Sender
     */
    public static Sender internal(String email, Long userId) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일 주소는 필수입니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("내부인인 경우 User ID는 필수입니다.");
        }
        return new Sender(email, userId);
    }

    /**
     * 시스템 발신자 생성 (외부인)
     * 일반적인 시스템 발신 메일 (예: noreply@example.com)
     *
     * @param email 이메일 주소
     * @return Sender
     */
    public static Sender system(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일 주소는 필수입니다.");
        }
        return new Sender(email, null);
    }
}


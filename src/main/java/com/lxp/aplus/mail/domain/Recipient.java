package com.lxp.aplus.mail.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * 메일 수신자 Value Object
 * 내부인(회원)과 외부인(비회원) 모두 지원
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Recipient {

    /**
     * 수신자 이메일 주소 (필수)
     */
    private final String email;

    /**
     * 수신자 User ID (선택적, 내부인인 경우에만 존재)
     */
    private final Long userId;

    /**
     * 내부인 여부
     * userId가 있으면 내부인(회원), 없으면 외부인(비회원)
     */
    public boolean isInternal() {
        return userId != null;
    }

    /**
     * 외부인 여부
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
     * 내부인(회원) 수신자 생성
     *
     * @param email 이메일 주소
     * @param userId User ID
     * @return Recipient
     */
    public static Recipient internal(String email, Long userId) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일 주소는 필수입니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("내부인인 경우 User ID는 필수입니다.");
        }
        return new Recipient(email, userId);
    }

    /**
     * 외부인(비회원) 수신자 생성
     *
     * @param email 이메일 주소
     * @return Recipient
     */
    public static Recipient external(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일 주소는 필수입니다.");
        }
        return new Recipient(email, null);
    }
}


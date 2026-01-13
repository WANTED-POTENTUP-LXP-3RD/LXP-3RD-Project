package com.lxp.aplus.mail.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메일 도메인 객체
 * 메일 발송에 필요한 정보를 담는 임시 객체 (RDB에 저장하지 않음)
 * 발송 후 사용하고 버리는 용도
 */
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Mail {

    /**
     * 수신자 (내부인/외부인 모두 지원)
     */
    private Recipient recipient;

    /**
     * 발신자 (내부인/외부인 모두 지원)
     */
    private Sender sender;

    /**
     * 메일 제목
     */
    private String subject;

    /**
     * 메일 내용 (HTML)
     */
    private String content;

    /**
     * 메일 생성
     *
     * @param recipient 수신자 (내부인/외부인)
     * @param sender 발신자 (시스템/내부인)
     * @param subject 메일 제목 (렌더링 완료된)
     * @param content 메일 내용 (HTML, 렌더링 완료된)
     */
    public static Mail create(
            Recipient recipient,
            Sender sender,
            String subject,
            String content
    ) {
        return Mail.builder()
                .recipient(recipient)
                .sender(sender)
                .subject(subject)
                .content(content)
                .build();
    }

    // ===== 편의 메서드 =====

    /**
     * 수신자 이메일 주소 조회
     */
    public String getRecipientEmail() {
        return recipient.getEmail();
    }

    /**
     * 발신자 이메일 주소 조회
     */
    public String getSenderEmail() {
        return sender.getEmail();
    }

    /**
     * 수신자가 내부인(회원)인지 확인
     */
    public boolean isRecipientInternal() {
        return recipient.isInternal();
    }

    /**
     * 수신자가 외부인(비회원)인지 확인
     */
    public boolean isRecipientExternal() {
        return recipient.isExternal();
    }

    /**
     * 수신자 User ID 조회 (내부인인 경우에만 존재)
     */
    public java.util.Optional<Long> getRecipientUserId() {
        return recipient.getUserId();
    }

    /**
     * 발신자가 내부인인지 확인
     */
    public boolean isSenderInternal() {
        return sender.isInternal();
    }

    /**
     * 발신자가 시스템 발신자인지 확인
     */
    public boolean isSenderSystem() {
        return sender.isExternal();
    }
}


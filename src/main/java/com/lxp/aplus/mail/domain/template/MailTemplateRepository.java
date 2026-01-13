package com.lxp.aplus.mail.domain.template;

import java.util.Optional;

/**
 * 메일 템플릿 Repository 인터페이스
 * 도메인 레이어에 위치한 순수 Java 인터페이스
 */
public interface MailTemplateRepository {
    /**
     * 템플릿 저장
     */
    MailTemplate save(MailTemplate template);

    /**
     * ID로 템플릿 조회
     */
    Optional<MailTemplate> findById(Long id);

    /**
     * 템플릿 코드로 템플릿 조회
     */
    Optional<MailTemplate> findByTemplateCode(MailTemplateCode templateCode);

    /**
     * 템플릿 존재 여부 확인
     */
    boolean existsById(Long id);

    /**
     * 템플릿 코드 존재 여부 확인
     */
    boolean existsByTemplateCode(MailTemplateCode templateCode);
}


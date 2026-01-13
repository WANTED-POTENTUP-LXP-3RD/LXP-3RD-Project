package com.lxp.aplus.mail.infrastructure.persistence;

import com.lxp.aplus.mail.domain.template.MailTemplate;
import com.lxp.aplus.mail.domain.template.MailTemplateCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * MailTemplate JPA Repository
 * Spring Data JPA가 자동으로 구현체 생성
 * 
 * 인프라스트럭처 레이어에 위치한 JPA 전용 Repository
 */
public interface MailTemplateJpaRepository extends JpaRepository<MailTemplate, Long> {
    
    /**
     * 템플릿 코드로 템플릿 조회
     */
    Optional<MailTemplate> findByTemplateCode(MailTemplateCode templateCode);
    
    /**
     * 템플릿 코드 존재 여부 확인
     */
    boolean existsByTemplateCode(MailTemplateCode templateCode);
}

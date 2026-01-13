package com.lxp.aplus.mail.infrastructure.persistence;

import com.lxp.aplus.mail.domain.template.MailTemplate;
import com.lxp.aplus.mail.domain.template.MailTemplateCode;
import com.lxp.aplus.mail.domain.template.MailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MailTemplateRepository 구현체
 * 
 * 도메인 레이어의 MailTemplateRepository 인터페이스를 구현.
 * MailTemplateJpaRepository를 사용하여 JPA 기능을 제공.
 */
@Repository
@RequiredArgsConstructor
public class MailTemplateRepositoryImpl implements MailTemplateRepository {
    
    private final MailTemplateJpaRepository jpaRepository;
    
    @Override
    public MailTemplate save(MailTemplate template) {
        return jpaRepository.save(template);
    }
    
    @Override
    public Optional<MailTemplate> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public Optional<MailTemplate> findByTemplateCode(MailTemplateCode templateCode) {
        return jpaRepository.findByTemplateCode(templateCode);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public boolean existsByTemplateCode(MailTemplateCode templateCode) {
        return jpaRepository.existsByTemplateCode(templateCode);
    }
}

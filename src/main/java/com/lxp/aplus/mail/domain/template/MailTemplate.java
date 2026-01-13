package com.lxp.aplus.mail.domain.template;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.mail.domain.template.variable.MailTemplateVariable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 메일 템플릿 엔티티
 * HTML 소스로 작성된 메일 템플릿을 RDB에 저장
 * 
 * 주의사항:
 * - templateCode는 유니크 제약조건이 있어 중복 생성 불가
 * - 같은 templateCode로 새 템플릿을 만들려면:
 *   1. 기존 템플릿을 물리 삭제 후 재생성, 또는
 *   2. 기존 템플릿을 update() 메서드로 업데이트
 */
@Builder
@Entity
@Table(
        name = "mail_templates",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_mail_template_code", columnNames = {"template_code"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MailTemplate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 템플릿 코드
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "template_code", nullable = false, length = 100)
    private MailTemplateCode templateCode;

    /**
     * 템플릿 이름
     */
    @Column(name = "template_name", nullable = false, length = 200)
    private String templateName;

    /**
     * 메일 제목 (변수 없이 고정 텍스트)
     */
    @Column(name = "subject", nullable = false, length = 500)
    private String subject;

    /**
     * HTML 소스 (템플릿 변수 포함 가능)
     * 
     * 주의: 템플릿 변수 구조는 Java 코드에서 정의됨 (variable 패키지)
     * 예: WelcomeMailVariable, PasswordResetVariable 등
     * DB에 중복 저장하지 않음 (단일 소스 원칙)
     */
    @Column(name = "html_content", columnDefinition = "TEXT", nullable = false)
    private String htmlContent;

    /**
     * 템플릿 생성
     * 
     * 주의: templateCode는 유니크 제약조건이 있어 중복 생성 불가
     * 같은 templateCode로 생성하려면:
     * - 기존 템플릿을 물리 삭제 후 재생성, 또는
     * - 기존 템플릿을 update() 메서드로 업데이트
     * 
     * @param templateCode 템플릿 코드 (유니크)
     * @param templateName 템플릿 이름
     * @param subject 메일 제목
     * @param htmlContent HTML 내용
     * @return MailTemplate
     */
    public static MailTemplate create(
            MailTemplateCode templateCode,
            String templateName,
            String subject,
            String htmlContent
    ) {
        return MailTemplate.builder()
                .templateCode(templateCode)
                .templateName(templateName)
                .subject(subject)
                .htmlContent(htmlContent)
                .build();
    }

    /**
     * 템플릿 업데이트
     * 
     * 기존 템플릿의 내용을 업데이트합니다.
     * templateCode는 변경할 수 없습니다 (유니크 제약조건).
     * 
     * @param templateName 템플릿 이름
     * @param subject 메일 제목
     * @param htmlContent HTML 내용
     */
    public void update(String templateName, String subject, String htmlContent) {
        this.templateName = templateName;
        this.subject = subject;
        this.htmlContent = htmlContent;
    }
    
    /**
     * 템플릿 렌더링 (내용)
     * 
     * 템플릿 변수를 HTML 내용에 주입하여 렌더링된 내용을 반환합니다.
     * 변수가 없는 경우 원본 내용을 그대로 반환합니다.
     * 
     * @param renderer 템플릿 렌더러 (포트 인터페이스)
     * @param variables 템플릿 변수 (null인 경우 변수 없이 렌더링)
     * @return 렌더링된 HTML 내용
     */
    public String renderContent(TemplateRenderer renderer, MailTemplateVariable variables) {
        if (variables == null)
            return this.htmlContent;

        return renderer.render(this.htmlContent, variables);
    }
}


package com.lxp.aplus.mail.domain.template;

import com.lxp.aplus.mail.domain.template.variable.MailTemplateVariable;

/**
 * 템플릿 렌더링 포트 인터페이스
 * 
 * 도메인 레이어에 위치한 순수 Java 인터페이스
 * MailTemplate이 자신을 렌더링하기 위해 사용
 */
public interface TemplateRenderer {
    
    /**
     * 템플릿 변수를 HTML 템플릿에 주입하여 렌더링
     * 
     * @param templateHtml HTML 템플릿 ({{변수명}} 형식의 플레이스홀더 포함)
     * @param variables 템플릿 변수
     * @return 렌더링된 HTML
     */
    String render(String templateHtml, MailTemplateVariable variables);
    
    /**
     * 제목 템플릿 렌더링
     * 
     * @param subjectTemplate 제목 템플릿 ({{변수명}} 형식의 플레이스홀더 포함)
     * @param variables 템플릿 변수
     * @return 렌더링된 제목
     */
    String renderSubject(String subjectTemplate, MailTemplateVariable variables);
}


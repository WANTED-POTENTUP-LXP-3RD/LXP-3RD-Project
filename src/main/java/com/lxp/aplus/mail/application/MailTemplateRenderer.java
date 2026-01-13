package com.lxp.aplus.mail.application;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.MailErrorCode;
import com.lxp.aplus.mail.domain.template.variable.MailTemplateVariable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 메일 템플릿 렌더링 서비스
 * 
 * 템플릿 변수를 HTML 템플릿에 주입하여 최종 메일 내용을 생성.
 * 현재는 간단한 String.replace 방식으로 구현되어 있으며,
 * 향후 FreeMarker 등 템플릿 엔진으로 교체 가능.
 * 
 * TemplateRenderer 포트 인터페이스의 구현체입니다.
 */
@Slf4j
@Component
public class MailTemplateRenderer implements com.lxp.aplus.mail.domain.template.TemplateRenderer {
    
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");
    
    /**
     * 템플릿 변수를 HTML 템플릿에 주입하여 렌더링
     * 
     * @param templateHtml HTML 템플릿 ({{변수명}} 형식의 플레이스홀더 포함)
     * @param variables 템플릿 변수
     * @return 렌더링된 HTML
     */
    public String render(String templateHtml, MailTemplateVariable variables) {
        if (templateHtml == null || templateHtml.isBlank()) {
            throw new IllegalArgumentException("템플릿 HTML이 비어있습니다.");
        }
        
        if (variables == null) {
            throw new IllegalArgumentException("템플릿 변수가 null입니다.");
        }
        
        Map<String, Object> variableMap = variables.toMap();
        String rendered = templateHtml;
        List<String> missingVariables = new ArrayList<>();
        
        // {{변수명}} 패턴을 찾아서 변수 값으로 치환
        Matcher matcher = VARIABLE_PATTERN.matcher(templateHtml);
        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            Object value = variableMap.get(variableName);
            
            if (value == null) {
                missingVariables.add(variableName);
            } else {
                rendered = rendered.replace("{{" + variableName + "}}", value.toString());
            }
        }
        
        // 누락된 변수가 있으면 예외 발생
        if (!missingVariables.isEmpty()) {
            log.error("템플릿 변수 누락: {}", missingVariables);
            throw new BusinessException(MailErrorCode.MISSING_TEMPLATE_VARIABLE);
        }
        
        return rendered;
    }
    
    /**
     * 제목 템플릿 렌더링
     * 
     * @param subjectTemplate 제목 템플릿 ({{변수명}} 형식의 플레이스홀더 포함)
     * @param variables 템플릿 변수
     * @return 렌더링된 제목
     */
    public String renderSubject(String subjectTemplate, MailTemplateVariable variables) {
        if (subjectTemplate == null || subjectTemplate.isBlank()) {
            throw new IllegalArgumentException("제목 템플릿이 비어있습니다.");
        }
        
        if (variables == null) {
            throw new IllegalArgumentException("템플릿 변수가 null입니다.");
        }
        
        Map<String, Object> variableMap = variables.toMap();
        String rendered = subjectTemplate;
        List<String> missingVariables = new ArrayList<>();
        
        Matcher matcher = VARIABLE_PATTERN.matcher(subjectTemplate);
        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            Object value = variableMap.get(variableName);
            
            if (value == null) {
                missingVariables.add(variableName);
            } else {
                rendered = rendered.replace("{{" + variableName + "}}", value.toString());
            }
        }
        
        // 누락된 변수가 있으면 예외 발생
        if (!missingVariables.isEmpty()) {
            log.error("제목 템플릿 변수 누락: {}", missingVariables);
            throw new BusinessException(MailErrorCode.MISSING_TEMPLATE_VARIABLE);
        }
        
        return rendered;
    }
}


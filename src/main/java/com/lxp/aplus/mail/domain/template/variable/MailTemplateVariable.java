package com.lxp.aplus.mail.domain.template.variable;

/**
 * 메일 템플릿 변수 공통 인터페이스
 * 각 템플릿별 변수 모델이 구현해야 하는 마커 인터페이스
 */
public interface MailTemplateVariable {
    /**
     * 템플릿 변수를 Map으로 변환 (템플릿 렌더링 시 사용)
     * 
     * @return 변수 Map
     */
    java.util.Map<String, Object> toMap();
}


package com.lxp.aplus.mail.domain.template;

import com.lxp.aplus.mail.domain.template.variable.MailTemplateVariable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MailTemplate 도메인 테스트")
class MailTemplateTest {

    @Test
    @DisplayName("템플릿을 생성할 수 있다")
    void create_success() {
        // when
        MailTemplate template = MailTemplate.create(
                MailTemplateCode.WELCOME_MAIL,
                "회원가입 환영 메일",
                "환영합니다!",
                "<html>환영 메일 내용</html>"
        );

        // then
        assertThat(template.getTemplateCode()).isEqualTo(MailTemplateCode.WELCOME_MAIL);
        assertThat(template.getTemplateName()).isEqualTo("회원가입 환영 메일");
        assertThat(template.getSubject()).isEqualTo("환영합니다!");
        assertThat(template.getHtmlContent()).isEqualTo("<html>환영 메일 내용</html>");
    }

    @Test
    @DisplayName("템플릿을 업데이트할 수 있다")
    void update_success() {
        // given
        MailTemplate template = MailTemplate.create(
                MailTemplateCode.WELCOME_MAIL,
                "회원가입 환영 메일",
                "환영합니다!",
                "<html>환영 메일 내용</html>"
        );

        // when
        template.update(
                "업데이트된 템플릿 이름",
                "업데이트된 제목",
                "<html>업데이트된 내용</html>"
        );

        // then
        assertThat(template.getTemplateCode()).isEqualTo(MailTemplateCode.WELCOME_MAIL); // 변경되지 않음
        assertThat(template.getTemplateName()).isEqualTo("업데이트된 템플릿 이름");
        assertThat(template.getSubject()).isEqualTo("업데이트된 제목");
        assertThat(template.getHtmlContent()).isEqualTo("<html>업데이트된 내용</html>");
    }

    @Test
    @DisplayName("변수가 없으면 원본 HTML 내용을 그대로 반환한다")
    void renderContent_success_when_variables_is_null() {
        // given
        MailTemplate template = MailTemplate.create(
                MailTemplateCode.WELCOME_MAIL,
                "회원가입 환영 메일",
                "환영합니다!",
                "<html>원본 내용</html>"
        );
        TemplateRenderer renderer = mock(TemplateRenderer.class);

        // when
        String rendered = template.renderContent(renderer, null);

        // then
        assertThat(rendered).isEqualTo("<html>원본 내용</html>");
        verify(renderer, never()).render(anyString(), any(MailTemplateVariable.class));
    }

    @Test
    @DisplayName("변수가 있으면 렌더러를 통해 렌더링된 내용을 반환한다")
    void renderContent_success_when_variables_exists() {
        // given
        MailTemplate template = MailTemplate.create(
                MailTemplateCode.WELCOME_MAIL,
                "회원가입 환영 메일",
                "환영합니다!",
                "<html>안녕하세요 {{userName}}님</html>"
        );
        TemplateRenderer renderer = mock(TemplateRenderer.class);
        MailTemplateVariable variables = mock(MailTemplateVariable.class);
        String expectedRendered = "<html>안녕하세요 홍길동님</html>";

        when(renderer.render(template.getHtmlContent(), variables))
                .thenReturn(expectedRendered);

        // when
        String rendered = template.renderContent(renderer, variables);

        // then
        assertThat(rendered).isEqualTo(expectedRendered);
        verify(renderer, times(1)).render(template.getHtmlContent(), variables);
    }
}


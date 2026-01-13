# Mail Template Variable 구조

## 개요
메일 템플릿 변수는 각 템플릿 코드별로 타입 안전한 변수 모델을 제공.
각 도메인에서 필요한 이메일 타입이 있다면 `MailTemplateCode` enum에 추가하고, 여기서 해당 템플릿 변수를 추가.

## 구조

### MailTemplateVariable (인터페이스)
- **역할**: 모든 템플릿 변수가 구현해야 하는 공통 인터페이스
- **메서드**: `toMap()` - 템플릿 렌더링에 사용할 Map으로 변환

### 템플릿별 변수 모델 (Record)
각 `MailTemplateCode`에 대응하는 변수 모델이 존재합니다.

| 템플릿 코드 | 변수 모델 | 주요 변수 |
|-----------|---------|----------|
| `WELCOME_MAIL` | `WelcomeMailVariable` | userName, loginUrl |
| `PASSWORD_RESET` | `PasswordResetVariable` | userName, resetLink, expirationMinutes |
| `EMAIL_VERIFICATION` | `EmailVerificationVariable` | userName, verificationLink, expirationMinutes |
| `ORDER_CONFIRMATION` | `OrderConfirmationVariable` | userName, orderId, orderDate, totalAmount, orderDetailUrl |
| `PAYMENT_COMPLETED` | `PaymentCompletedVariable` | userName, orderId, paymentDate, paymentAmount, paymentMethod, courseNames |

## 새 템플릿 추가 방법

### 1. MailTemplateCode Enum에 추가
```java
// MailTemplateCode.java
public enum MailTemplateCode {
    // ... 기존 코드들
    NEW_TEMPLATE("새 템플릿 설명");
}
```

### 2. 템플릿 변수 모델 생성
```java
// variable/NewTemplateVariable.java
public record NewTemplateVariable(
        String userName,
        String customField
) implements MailTemplateVariable {
    
    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "userName", userName,
                "customField", customField
        );
    }
}
```

### 3. MailTemplate 엔티티에 템플릿 데이터 저장
- `MailTemplate.create()` 메서드를 통해 템플릿 생성
- `templateCode`는 유니크 제약조건이 있어 중복 생성 불가
- 기존 템플릿이 있으면 `update()` 메서드로 업데이트

## 사용 예시

### 변수 생성
```java
// 회원가입 환영 메일 변수
WelcomeMailVariable variables = new WelcomeMailVariable(
    "홍길동",
    "https://example.com/login"
);

// 비밀번호 재설정 메일 변수
PasswordResetVariable resetVars = new PasswordResetVariable(
    "홍길동",
    "https://example.com/reset?token=xxx",
    30
);
```

### 템플릿 렌더링
```java
// 변수를 Map으로 변환하여 템플릿 엔진에 전달
Map<String, Object> variableMap = variables.toMap();
// {"userName": "홍길동", "loginUrl": "https://example.com/login"}

// 템플릿 렌더링
// TODO: 템플릿 엔진 구현 필요 (Thymeleaf, FreeMarker, 또는 간단한 String.replace 등)
// 예시:
// String renderedContent = templateEngine.process(template.getHtmlContent(), variableMap);
// 또는 간단한 구현:
// String renderedContent = template.getHtmlContent()
//     .replace("{{userName}}", variables.userName())
//     .replace("{{loginUrl}}", variables.loginUrl());
```

## 설계 원칙

### 타입 안전성
- 각 템플릿별로 명시적인 변수 타입 정의
- 컴파일 타임에 변수 검증 가능

### 확장성
- 새 템플릿 추가 시 새 Record만 생성하면 됨
- 기존 코드에 영향 없음
- 공통 인터페이스로 일관성 유지

### 명확성
- 각 템플릿의 필수 변수가 명시적으로 정의됨
- 변수 이름과 타입이 명확함
- 문서화가 코드에 포함됨

## 패키지 구조
```
com.lxp.aplus.mail.domain.template.variable
├── MailTemplateVariable.java          # 공통 인터페이스
├── WelcomeMailVariable.java          # 회원가입 환영 메일
├── PasswordResetVariable.java        # 비밀번호 재설정
├── EmailVerificationVariable.java    # 이메일 인증
├── OrderConfirmationVariable.java    # 주문 확인
└── PaymentCompletedVariable.java     # 결제 완료
```

## 주의사항

1. **템플릿 코드와 변수 모델 일치**
   - `MailTemplateCode`에 추가한 코드에 대응하는 변수 모델을 반드시 생성해야 함
   - 변수 모델이 없으면 템플릿 렌더링 시 오류 발생 가능

2. **변수 이름 일관성**
   - 템플릿 HTML의 변수 이름과 `toMap()`의 키 이름이 일치해야 함
   - 예: HTML에서 `{{userName}}`을 사용하면 Map의 키도 `"userName"`이어야 함

3. **필수 변수 검증**
   - 변수 모델 생성 시 필수 필드가 null이면 안 됨
   - 필요시 변수 모델에 검증 로직 추가 고려


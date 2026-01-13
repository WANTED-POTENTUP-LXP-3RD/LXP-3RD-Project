-- 메일 템플릿 초기 데이터 삽입

-- 1. 회원가입 환영 메일
INSERT INTO mail_templates (template_code, template_name, subject, html_content, created_at, updated_at)
VALUES (
    'WELCOME_MAIL',
    '회원가입 환영 메일',
    '회원가입을 환영합니다!',
    '<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 환영 메일</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: #ffffff;
            border-radius: 8px;
            padding: 40px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        .logo {
            font-size: 24px;
            font-weight: bold;
            color: #EF6817;
            margin-bottom: 10px;
        }
        .title {
            font-size: 20px;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
        }
        .content {
            margin-bottom: 30px;
        }
        .greeting {
            font-size: 16px;
            margin-bottom: 20px;
        }
        .message {
            font-size: 14px;
            color: #666;
            margin-bottom: 30px;
            line-height: 1.8;
        }
        .button {
            display: inline-block;
            padding: 12px 30px;
            background-color: #EF6817;
            color: #ffffff;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 600;
            text-align: center;
            margin: 20px 0;
        }
        .button:hover {
            background-color: #C85412;
        }
        .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
            text-align: center;
            font-size: 12px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">Lernix</div>
            <div class="title">회원가입을 환영합니다!</div>
        </div>
        
        <div class="content">
            <div class="greeting">
                안녕하세요, <strong>{{userName}}</strong>님!
            </div>
            
            <div class="message">
                Lernix에 가입해 주셔서 감사합니다.<br>
                이제 다양한 강좌를 수강하고 새로운 지식을 습득하실 수 있습니다.<br><br>
                아래 버튼을 클릭하여 로그인하시고 서비스를 이용해 보세요.
            </div>
            
            <div style="text-align: center;">
                <a href="{{loginUrl}}" class="button">로그인하기</a>
            </div>
        </div>
        
        <div class="footer">
            <p>이 메일은 회원가입 시 자동으로 발송되는 메일입니다.</p>
            <p>© 2026 Lernix. All rights reserved.</p>
        </div>
    </div>
</body>
</html>',
    NOW(),
    NOW()
);

-- 2. 이메일 인증 메일
INSERT INTO mail_templates (template_code, template_name, subject, html_content, created_at, updated_at)
VALUES (
    'EMAIL_VERIFICATION',
    '이메일 인증',
    '이메일 인증',
    '<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>이메일 인증</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: #ffffff;
            border: 1px solid #E5E7EB;
            border-radius: 8px;
            padding: 40px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        .logo {
            font-size: 32px;
            font-weight: bold;
            color: #EF6817;
            margin-bottom: 10px;
        }
        .title {
            font-size: 20px;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
        }
        .content {
            margin-bottom: 30px;
        }
        .greeting {
            font-size: 16px;
            margin-bottom: 20px;
        }
        .message {
            font-size: 14px;
            color: #666;
            margin-bottom: 20px;
            line-height: 1.8;
        }
        .info {
            background-color: #F5F7FA;
            border: 1px solid #E5E7EB;
            border-left: 4px solid #6B7280;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
            font-size: 13px;
            color: #374151;
            text-align: center;
        }
        .button {
            display: inline-block;
            padding: 12px 30px;
            background-color: #EF6817;
            color: #ffffff !important;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 600;
            text-align: center;
            margin: 20px 0;
        }
        .button:link,
        .button:visited,
        .button:hover,
        .button:active {
            color: #ffffff !important;
            text-decoration: none;
        }
        .button:hover {
            background-color: #C85412;
        }
        .expiration {
            font-size: 12px;
            color: #999;
            margin-top: 10px;
        }
        .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
            text-align: center;
            font-size: 12px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">Lernix</div>
            <div class="title">✉️ 이메일 인증</div>
        </div>
        
        <div class="content">
            <div class="greeting">
                👋 안녕하세요, <strong>{{userName}}</strong>님!
            </div>
            
            <div class="message">
                🔐 회원가입을 완료하기 위해 이메일 인증이 필요합니다.<br>
                아래 버튼을 클릭하여 이메일 인증을 완료해 주세요.
            </div>
            
            <div style="text-align: center;">
                <a href="{{verificationLink}}" class="button">✅ 이메일 인증하기</a>
            </div>
            
            <div class="info">
                <strong>ℹ️ 안내</strong><br>
                ⏰ 인증 링크는 {{expirationMinutes}}분 동안만 유효합니다.<br>
                🔒 인증을 완료하지 않으면 회원가입이 불가합니다.<br>
                🚫 인증 링크는 타인에게 공유하지 마세요.
            </div>
            
            <div class="expiration" style="text-align: center;">
                🔗 링크가 작동하지 않을 경우, 아래 주소를 복사하여 브라우저에 붙여넣으세요:<br>
                <span style="word-break: break-all; color: #EF6817;">{{verificationLink}}</span>
            </div>
        </div>
        
        <div class="footer">
            <p>이 메일은 회원가입 시 자동으로 발송되는 메일입니다.</p>
            <p>© 2026 Lernix. All rights reserved.</p>
        </div>
    </div>
</body>
</html>',
    NOW(),
    NOW()
);

-- 3. 비밀번호 재설정 메일
INSERT INTO mail_templates (template_code, template_name, subject, html_content, created_at, updated_at)
VALUES (
    'PASSWORD_RESET',
    '비밀번호 재설정',
    '비밀번호 재설정',
    '<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>비밀번호 재설정</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: #ffffff;
            border-radius: 8px;
            padding: 40px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        .logo {
            font-size: 24px;
            font-weight: bold;
            color: #EF6817;
            margin-bottom: 10px;
        }
        .title {
            font-size: 20px;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
        }
        .content {
            margin-bottom: 30px;
        }
        .greeting {
            font-size: 16px;
            margin-bottom: 20px;
        }
        .message {
            font-size: 14px;
            color: #666;
            margin-bottom: 20px;
            line-height: 1.8;
        }
        .warning {
            background-color: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
            font-size: 13px;
            color: #856404;
        }
        .button {
            display: inline-block;
            padding: 12px 30px;
            background-color: #EF6817;
            color: #ffffff;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 600;
            text-align: center;
            margin: 20px 0;
        }
        .button:hover {
            background-color: #C85412;
        }
        .expiration {
            font-size: 12px;
            color: #999;
            margin-top: 10px;
        }
        .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
            text-align: center;
            font-size: 12px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">Lernix</div>
            <div class="title">비밀번호 재설정</div>
        </div>
        
        <div class="content">
            <div class="greeting">
                안녕하세요, <strong>{{userName}}</strong>님!
            </div>
            
            <div class="message">
                비밀번호 재설정을 요청하셨습니다.<br>
                아래 버튼을 클릭하여 새로운 비밀번호를 설정해 주세요.
            </div>
            
            <div style="text-align: center;">
                <a href="{{resetLink}}" class="button">비밀번호 재설정하기</a>
            </div>
            
            <div class="warning">
                <strong>⚠️ 보안 안내</strong><br>
                • 이 링크는 {{expirationMinutes}}분 동안만 유효합니다.<br>
                • 본인이 요청하지 않으셨다면 이 메일을 무시하셔도 됩니다.<br>
                • 비밀번호 재설정 링크는 타인에게 공유하지 마세요.
            </div>
            
            <div class="expiration" style="text-align: center;">
                링크가 작동하지 않을 경우, 아래 주소를 복사하여 브라우저에 붙여넣으세요:<br>
                <span style="word-break: break-all; color: #EF6817;">{{resetLink}}</span>
            </div>
        </div>
        
        <div class="footer">
            <p>이 메일은 비밀번호 재설정 요청 시 자동으로 발송되는 메일입니다.</p>
            <p>© 2026 Lernix. All rights reserved.</p>
        </div>
    </div>
</body>
</html>',
    NOW(),
    NOW()
);

-- 4. 주문 확인 메일
INSERT INTO mail_templates (template_code, template_name, subject, html_content, created_at, updated_at)
VALUES (
    'ORDER_CONFIRMATION',
    '주문 확인',
    '주문이 완료되었습니다',
    '<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주문 확인</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: #ffffff;
            border-radius: 8px;
            padding: 40px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        .logo {
            font-size: 24px;
            font-weight: bold;
            color: #EF6817;
            margin-bottom: 10px;
        }
        .title {
            font-size: 20px;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
        }
        .content {
            margin-bottom: 30px;
        }
        .greeting {
            font-size: 16px;
            margin-bottom: 20px;
        }
        .message {
            font-size: 14px;
            color: #666;
            margin-bottom: 30px;
            line-height: 1.8;
        }
        .order-info {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            margin: 20px 0;
        }
        .order-info-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #e0e0e0;
        }
        .order-info-row:last-child {
            border-bottom: none;
        }
        .order-info-label {
            font-weight: 600;
            color: #666;
        }
        .order-info-value {
            color: #333;
            text-align: right;
        }
        .total-amount {
            font-size: 18px;
            font-weight: bold;
            color: #EF6817;
        }
        .button {
            display: inline-block;
            padding: 12px 30px;
            background-color: #EF6817;
            color: #ffffff;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 600;
            text-align: center;
            margin: 20px 0;
        }
        .button:hover {
            background-color: #C85412;
        }
        .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
            text-align: center;
            font-size: 12px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">Lernix</div>
            <div class="title">주문이 완료되었습니다</div>
        </div>
        
        <div class="content">
            <div class="greeting">
                안녕하세요, <strong>{{userName}}</strong>님!
            </div>
            
            <div class="message">
                주문이 성공적으로 완료되었습니다.<br>
                주문 내역은 아래와 같습니다.
            </div>
            
            <div class="order-info">
                <div class="order-info-row">
                    <span class="order-info-label">주문번호</span>
                    <span class="order-info-value">{{orderId}}</span>
                </div>
                <div class="order-info-row">
                    <span class="order-info-label">주문일시</span>
                    <span class="order-info-value">{{orderDate}}</span>
                </div>
                <div class="order-info-row" style="border-top: 2px solid #EF6817; padding-top: 15px; margin-top: 10px;">
                    <span class="order-info-label">총 결제금액</span>
                    <span class="order-info-value total-amount">{{totalAmount}}원</span>
                </div>
            </div>
            
            <div style="text-align: center;">
                <a href="{{orderDetailUrl}}" class="button">주문 상세보기</a>
            </div>
        </div>
        
        <div class="footer">
            <p>주문 관련 문의사항이 있으시면 고객센터로 연락해 주세요.</p>
            <p>© 2026 Lernix. All rights reserved.</p>
        </div>
    </div>
</body>
</html>',
    NOW(),
    NOW()
);

-- 5. 결제 완료 메일
INSERT INTO mail_templates (template_code, template_name, subject, html_content, created_at, updated_at)
VALUES (
    'PAYMENT_COMPLETED',
    '결제 완료',
    '결제가 완료되었습니다',
    '<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>결제 완료</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: #ffffff;
            border-radius: 8px;
            padding: 40px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        .logo {
            font-size: 24px;
            font-weight: bold;
            color: #EF6817;
            margin-bottom: 10px;
        }
        .title {
            font-size: 20px;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
        }
        .success-badge {
            display: inline-block;
            background-color: #28a745;
            color: #ffffff;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 20px;
        }
        .content {
            margin-bottom: 30px;
        }
        .greeting {
            font-size: 16px;
            margin-bottom: 20px;
        }
        .message {
            font-size: 14px;
            color: #666;
            margin-bottom: 30px;
            line-height: 1.8;
        }
        .payment-info {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            margin: 20px 0;
        }
        .payment-info-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #e0e0e0;
        }
        .payment-info-row:last-child {
            border-bottom: none;
        }
        .payment-info-label {
            font-weight: 600;
            color: #666;
        }
        .payment-info-value {
            color: #333;
            text-align: right;
        }
        .course-list {
            background-color: #ffffff;
            border: 1px solid #e0e0e0;
            border-radius: 4px;
            padding: 15px;
            margin: 15px 0;
        }
        .course-item {
            padding: 8px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        .course-item:last-child {
            border-bottom: none;
        }
        .total-amount {
            font-size: 18px;
            font-weight: bold;
            color: #EF6817;
        }
        .button {
            display: inline-block;
            padding: 12px 30px;
            background-color: #EF6817;
            color: #ffffff;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 600;
            text-align: center;
            margin: 20px 0;
        }
        .button:hover {
            background-color: #C85412;
        }
        .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
            text-align: center;
            font-size: 12px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">Lernix</div>
            <div class="title">결제가 완료되었습니다</div>
            <div class="success-badge">✓ 결제 완료</div>
        </div>
        
        <div class="content">
            <div class="greeting">
                안녕하세요, <strong>{{userName}}</strong>님!
            </div>
            
            <div class="message">
                결제가 성공적으로 완료되었습니다.<br>
                이제 구매하신 강좌를 수강하실 수 있습니다.
            </div>
            
            <div class="payment-info">
                <div class="payment-info-row">
                    <span class="payment-info-label">주문번호</span>
                    <span class="payment-info-value">{{orderId}}</span>
                </div>
                <div class="payment-info-row">
                    <span class="payment-info-label">결제일시</span>
                    <span class="payment-info-value">{{paymentDate}}</span>
                </div>
                <div class="payment-info-row">
                    <span class="payment-info-label">결제수단</span>
                    <span class="payment-info-value">{{paymentMethod}}</span>
                </div>
                <div class="course-list">
                    <div style="font-weight: 600; margin-bottom: 10px; color: #333;">구매 강좌</div>
                    <div class="course-item">{{courseNames}}</div>
                </div>
                <div class="payment-info-row" style="border-top: 2px solid #EF6817; padding-top: 15px; margin-top: 10px;">
                    <span class="payment-info-label">결제금액</span>
                    <span class="payment-info-value total-amount">{{paymentAmount}}원</span>
                </div>
            </div>
            
            <div style="text-align: center; margin-top: 30px;">
                <p style="font-size: 14px; color: #666; margin-bottom: 20px;">
                    구매하신 강좌는 마이페이지에서 확인하실 수 있습니다.
                </p>
            </div>
        </div>
        
        <div class="footer">
            <p>결제 관련 문의사항이 있으시면 고객센터로 연락해 주세요.</p>
            <p>© 2026 Lernix. All rights reserved.</p>
        </div>
    </div>
</body>
</html>',
    NOW(),
    NOW()
);


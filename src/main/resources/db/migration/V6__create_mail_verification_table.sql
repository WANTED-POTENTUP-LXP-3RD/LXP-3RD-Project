create table if not exists mail_templates
(
    id            bigint auto_increment
    primary key,
    created_at    datetime(6)                                                                                              not null,
    updated_at    datetime(6)                                                                                              not null,
    html_content  text                                                                                                     not null,
    subject       varchar(500)                                                                                             not null,
    template_code enum ('EMAIL_VERIFICATION', 'ORDER_CONFIRMATION', 'PASSWORD_RESET', 'PAYMENT_COMPLETED', 'WELCOME_MAIL') not null,
    template_name varchar(200)                                                                                             not null,
    constraint uk_mail_template_code
    unique (template_code)
    );


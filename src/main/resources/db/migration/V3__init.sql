create table if not exists categories
(
    created_at datetime(6)  not null,
    id         bigint auto_increment
        primary key,
    parent_id  bigint       null,
    updated_at datetime(6)  not null,
    name       varchar(255) not null,
    constraint FKsaok720gsu4u2wrgbk10b5n8d
        foreign key (parent_id) references categories (id)
);

create table if not exists courses
(
    price         int                                                     not null,
    category_id   bigint                                                  not null,
    created_at    datetime(6)                                             not null,
    id            bigint auto_increment
        primary key,
    instructor_id bigint                                                  not null,
    updated_at    datetime(6)                                             not null,
    description   text                                                    not null,
    summary       varchar(255)                                            not null,
    thumbnail_url varchar(255)                                            not null,
    title         varchar(255)                                            not null,
    course_level  enum ('ADVANCED', 'BEGINNER', 'INTERMEDIATE', 'NOVICE') not null,
    course_status enum ('DELETED', 'DRAFT', 'PUBLISHED')                  not null
);

create table if not exists enrollments
(
    course_id  bigint                                                not null,
    created_at datetime(6)                                           not null,
    expired_at datetime(6)                                           not null,
    id         bigint auto_increment
        primary key,
    student_id bigint                                                not null,
    updated_at datetime(6)                                           not null,
    status     enum ('CANCELED', 'COMPLETED', 'ENROLLED', 'EXPIRED') null,
    constraint uk_enrollment_student_course
        unique (student_id, course_id)
);

create table if not exists orders
(
    amount              decimal(19)                                                 not null,
    completed_at        datetime(6)                                                 null,
    created_at          datetime(6)                                                 not null,
    updated_at          datetime(6)                                                 not null,
    user_id             bigint                                                      not null,
    approved_payment_id varchar(255)                                                null,
    cancel_reason       varchar(255)                                                null,
    currency            varchar(255)                                                not null,
    id                  varchar(255)                                                not null
        primary key,
    order_status        enum ('CANCELED', 'COMPLETED', 'PENDING') default 'PENDING' not null
);

create table if not exists order_lines
(
    price     decimal         not null,
    item_id   bigint          null,
    order_id  varchar(255)    not null,
    item_type enum ('COURSE') null,
    constraint FK1smc0s578t2oih21yn9hw6usr
        foreign key (order_id) references orders (id)
);

create table if not exists payments
(
    amount         decimal(19)                                                    not null,
    currency       varchar(3)                                                     not null,
    approved_at    datetime(6)                                                    null,
    canceled_at    datetime(6)                                                    null,
    created_at     datetime(6)                                                    not null,
    refunded_at    datetime(6)                                                    null,
    updated_at     datetime(6)                                                    not null,
    user_id        bigint                                                         not null,
    id             varchar(255)                                                   not null
        primary key,
    order_id       varchar(255)                                                   not null,
    payment_key    varchar(255)                                                   null,
    payment_method enum ('CARD')                                                  not null,
    payment_status enum ('APPROVED', 'CANCELED', 'FAILED', 'PENDING', 'REFUNDED') not null,
    pg_provider    enum ('TOSS')                                                  not null,
    constraint uk_payment_key
        unique (payment_key)
);

create table if not exists sections
(
    order_index int          not null,
    course_id   bigint       not null,
    created_at  datetime(6)  not null,
    id          bigint auto_increment
        primary key,
    updated_at  datetime(6)  not null,
    title       varchar(255) not null,
    constraint FK7ty9cevpq04d90ohtso1q8312
        foreign key (course_id) references courses (id)
);

create table if not exists lectures
(
    is_preview             bit          not null,
    order_index            int          not null,
    total_duration_seconds int          null,
    created_at             datetime(6)  not null,
    id                     bigint auto_increment
        primary key,
    section_id             bigint       not null,
    updated_at             datetime(6)  not null,
    title                  varchar(255) not null,
    constraint FK69sw3j0r9oaugk25btekv616j
        foreign key (section_id) references sections (id),
    check (`order_index` >= 1)
);

create table if not exists lecture_resources
(
    is_downloadable    bit                                 null,
    created_at         datetime(6)                         not null,
    id                 bigint auto_increment
        primary key,
    lecture_id         bigint                              not null,
    updated_at         datetime(6)                         not null,
    file_key           varchar(255)                        not null,
    file_url           varchar(255)                        not null,
    original_file_name varchar(255)                        not null,
    extension_type     enum ('DOC', 'MP4', 'PDF', 'ZIP')   not null,
    resource_type      enum ('DOC', 'PDF', 'VIDEO', 'ZIP') not null,
    constraint FKa42txpoawbrkifkn7lamleugw
        foreign key (lecture_id) references lectures (id)
);

create table if not exists progresses
(
    is_completed        bit         not null,
    watched_duration    int         not null,
    created_at          datetime(6) not null,
    enrollment_id       bigint      not null,
    last_watched_at     datetime(6) null,
    lecture_resource_id bigint      not null,
    progress_id         bigint auto_increment
        primary key,
    updated_at          datetime(6) not null,
    constraint FK56ncysrwwhk5w9vr4ir6ktc52
        foreign key (lecture_resource_id) references lecture_resources (id),
    constraint FKoharntf7r92091t2dt9gfgtdt
        foreign key (enrollment_id) references enrollments (id)
);

create table if not exists users
(
    created_at   datetime(6)                                                                           not null,
    deleted_at   datetime(6)                                                                           null,
    id           bigint auto_increment
        primary key,
    updated_at   datetime(6)                                                                           not null,
    email        varchar(255)                                                                          null,
    name         varchar(255)                                                                          null,
    nick_name    varchar(255)                                                                          null,
    password     varchar(255)                                                                          null,
    phone_number varchar(255)                                                                          null,
    status       enum ('ACTIVE', 'BANNED', 'BLOCKED', 'INACTIVE', 'PENDING', 'SUSPENDED', 'WITHDRAWN') not null,
    constraint uk_user_email
        unique (email)
);

create table if not exists roles
(
    created_at datetime(6)                             not null,
    deleted_at datetime(6)                             null,
    id         bigint auto_increment
        primary key,
    user_id    bigint                                  null,
    role_type  enum ('ADMIN', 'INSTRUCTOR', 'STUDENT') not null,
    constraint UKj7h8ecptsbuenb68vu6rf27ns
        unique (user_id, role_type),
    constraint FK97mxvrajhkq19dmvboprimeg1
        foreign key (user_id) references users (id)
);

CREATE TABLE if not exists carts
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT uk_carts_user UNIQUE (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE if not exists cart_items
(
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT uk_cart_course UNIQUE (cart_id, course_id),
    CONSTRAINT fk_cart_items_cart_id FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
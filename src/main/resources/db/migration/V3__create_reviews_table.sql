create table if not exists reviews
(
    id         bigint auto_increment
    primary key,
    created_at datetime(6)                                        not null,
    updated_at datetime(6)                                        not null,
    content    varchar(2000)                                      not null,
    course_id  bigint                                             not null,
    deleted_at datetime(6)                                        null,
    rating     tinyint unsigned                                   not null,
    reported   int                                                not null,
    status     enum ('ARCHIVED', 'BLINDED', 'DELETED', 'DISPLAY') not null,
    user_id    bigint                                             not null
    );

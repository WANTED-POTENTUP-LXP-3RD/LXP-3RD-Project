create table if not exists lecture_resources_v2
(
    id                    bigint          NOT NULL AUTO_INCREMENT,
    created_at            datetime(6)     NOT NULL,
    updated_at            datetime(6)     NOT NULL,
    file_key              varchar(255)    NOT NULL,
    is_downloadable       bit(1)          DEFAULT NULL,
    original_file_name    varchar(255)    NOT NULL,
    resource_type         enum('DOC','PDF','VIDEO','ZIP') NOT NULL,
    duration              int             DEFAULT NULL,
    PRIMARY KEY (`id`)
);


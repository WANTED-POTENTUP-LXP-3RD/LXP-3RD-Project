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

INSERT INTO LECTURE_RESOURCES_V2 (created_at, updated_at, file_key, is_downloadable, original_file_name, resource_type, duration)
SELECT
    lr.created_at,
    lr.updated_at,
    lr.file_key,
    lr.is_downloadable,
    lr.original_file_name,
    lr.resource_type,
    l.total_duration_seconds
FROM lecture_resources lr
         JOIN lectures l ON lr.lecture_id = l.id;

SELECT @max_id := COALESCE(MAX(id), 0) + 1 FROM lecture_resources_v2;
SET @sql = CONCAT('ALTER TABLE lecture_resources_v2 AUTO_INCREMENT = ', @max_id);
PREPARE STMT FROM @sql;
EXECUTE STMT;
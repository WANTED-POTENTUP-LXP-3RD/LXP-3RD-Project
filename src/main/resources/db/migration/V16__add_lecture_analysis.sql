ALTER TABLE lecture_resources_v2
    ADD COLUMN analysis_status VARCHAR(20) NOT NULL DEFAULT 'NONE';

CREATE TABLE IF NOT EXISTS lecture_keywords
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    lecture_resource_id BIGINT NOT NULL,
    keyword VARCHAR(120) NOT NULL,
    importance DECIMAL(5,4) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_lecture_keywords_lecture_resource
        FOREIGN KEY (lecture_resource_id) REFERENCES lecture_resources_v2(id)
);

CREATE INDEX idx_lecture_keywords_lecture_resource_id
    ON lecture_keywords(lecture_resource_id);

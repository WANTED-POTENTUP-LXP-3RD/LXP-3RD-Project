-- Course 테이블의 thumbnail_url을 thumbnail_key로 변경
ALTER TABLE courses
    CHANGE COLUMN thumbnail_url thumbnail_key VARCHAR(255) NULL;

-- lecture의 lecture_resources 제약 조건 삭제
ALTER TABLE lecture_resources
DROP FOREIGN KEY FKa42txpoawbrkifkn7lamleugw;

ALTER TABLE lecture_resources
DROP INDEX FKa42txpoawbrkifkn7lamleugw;

-- lecture_resources_v2에 lecture_id 추가
ALTER TABLE lecture_resources_v2
    ADD COLUMN lecture_id BIGINT NULL;

ALTER TABLE lecture_resources_v2
    ADD CONSTRAINT fk_lecture_resources_v2_lecture
        FOREIGN KEY (lecture_id) REFERENCES lectures(id);

CREATE INDEX idx_lecture_resources_v2_lecture_id
    ON lecture_resources_v2(lecture_id);

--
ALTER TABLE enrollments
    MODIFY COLUMN order_item_id BIGINT NULL;
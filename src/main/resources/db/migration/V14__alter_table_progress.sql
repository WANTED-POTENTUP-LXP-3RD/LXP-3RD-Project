ALTER TABLE progresses DROP FOREIGN KEY FK56ncysrwwhk5w9vr4ir6ktc52;

-- 2. lecture_resources_v2 테이블을 참조하는 새로운 제약 조건 추가
ALTER TABLE progresses
    ADD CONSTRAINT FK_PROGRESS_ON_LECTURE_RESOURCE_V2
        FOREIGN KEY (lecture_resource_id)
            REFERENCES lecture_resources_v2 (id);
INSERT IGNORE INTO lecture_resources_v2 (created_at, updated_at, file_key, is_downloadable, original_file_name, resource_type, duration)
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

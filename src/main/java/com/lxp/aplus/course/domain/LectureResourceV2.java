package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Getter
@Table(name = "lecture_resources_v2")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureResourceV2 extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "duration")
    private Integer videoDuration;

    @Column(name = "is_downloadable")
    private boolean isDownloadable;

    private LectureResourceV2(
            String originalFileName,
            String fileKey,
            ResourceType resourceType,
            Integer videoDuration,
            boolean isDownloadable
    ) {
        this.originalFileName = originalFileName;
        this.fileKey = fileKey;
        this.resourceType = resourceType;
        this.videoDuration = videoDuration;
        this.isDownloadable = isDownloadable;
    }

    private static final Map<ResourceType, Long> MAX_FILE_SIZE = Map.of(
            ResourceType.VIDEO, 1000000L,
            ResourceType.ZIP, 1000000L,
            ResourceType.PDF, 50000L,
            ResourceType.DOC, 50000L
    );

    public static LectureResourceV2 create(
            String originalFileName,
            String fileKey,
            Integer videoDuration,
            boolean isDownloadable,
            long fileSize
    ) {

        ExtensionType extensionType = ExtensionType.fromFileName(originalFileName);
        ResourceType resourceType = ResourceType.fromExtension(extensionType.getExtension());

        validateVideoDuration(resourceType, videoDuration);
        validateFileSize(resourceType, fileSize);

        return new LectureResourceV2(
                originalFileName,
                fileKey,
                resourceType,
                videoDuration,
                isDownloadable
        );
    }

    private static void validateVideoDuration(ResourceType resourceType, Integer videoDuration) {
        if (resourceType == ResourceType.VIDEO && (videoDuration == null || videoDuration <= 0)) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_VIDEO_DURATION_NOT_FOUND);
        }
    }

    private static void validateFileSize(ResourceType resourceType, long fileSize) {
        Long maxSize = MAX_FILE_SIZE.get(resourceType);
        if (maxSize != null && fileSize > maxSize) {
            throw new BusinessException(LectureResourceErrorCode.FILE_SIZE_EXCEEDED);
        }
    }
}

package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
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

    public static LectureResourceV2 create(
            String originalFileName,
            String fileKey,
            Integer videoDuration,
            boolean isDownloadable
    ) {
        ExtensionType extensionType = ExtensionType.fromFileName(originalFileName);
        ResourceType resourceType = ResourceType.fromExtension(extensionType.getExtension());

        if ((resourceType == ResourceType.VIDEO) && (videoDuration == null || videoDuration <= 0)) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_VIDEO_DURATION_NOT_FOUND);
        }

        return new LectureResourceV2(
                originalFileName,
                fileKey,
                resourceType,
                videoDuration,
                isDownloadable
        );
    }
}

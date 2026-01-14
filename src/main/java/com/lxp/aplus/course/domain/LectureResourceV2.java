package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Embedded
    private VideoDuration videoDuration;

    @Column(name = "is_downloadable")
    private boolean isDownloadable;

    private LectureResourceV2(
            String originalFileName,
            String fileKey,
            ResourceType resourceType,
            VideoDuration videoDuration,
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
            Integer duration,
            boolean isDownloadable,
            long fileSize
    ) {

        ExtensionType extensionType = ExtensionType.fromFileName(originalFileName);
        ResourceType resourceType = ResourceType.fromExtension(extensionType.getExtension());
        resourceType.validateFileSize(fileSize);

        VideoDuration videoDuration = resourceType == ResourceType.VIDEO ? VideoDuration.from(duration) : null;

        return new LectureResourceV2(
                originalFileName,
                fileKey,
                resourceType,
                videoDuration,
                isDownloadable
        );
    }

    void assignToLecture(Lecture lecture) {
        this.lecture = lecture;
    }
}

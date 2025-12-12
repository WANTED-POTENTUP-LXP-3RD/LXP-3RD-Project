package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Table(name = "lecture_resources")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureResource extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "is_downloadable")
    private boolean isDownloadable;

    @Enumerated(EnumType.STRING)
    @Column(name = "extension_type", nullable = false)
    private ExtensionType extensionType;

    public static LectureResource create(Lecture lecture, boolean isDownloadable, String fileKey, String fileUrl, String originFileName) {
        ExtensionType extensionType = ExtensionType.fromFileName(originFileName);
        ResourceType resourceType = ResourceType.fromExtension(extensionType.getExtension());

        return LectureResource.builder()
                .lecture(lecture)
                .isDownloadable(isDownloadable)
                .fileUrl(fileUrl)
                .fileKey(fileKey)
                .originalFileName(originFileName)
                .extensionType(extensionType)
                .resourceType(resourceType)
                .build();
    }
}



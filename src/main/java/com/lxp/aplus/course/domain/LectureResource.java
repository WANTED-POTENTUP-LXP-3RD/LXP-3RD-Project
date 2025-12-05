package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lecture_resources")
@NoArgsConstructor
public class LectureResource extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "file_key")
    private String fileKey;

    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "is_preview")
    private boolean isPreview;

    @Column(name = "is_downloadable")
    private boolean isDownloadable;
}

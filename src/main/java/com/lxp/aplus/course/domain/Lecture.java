package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureErrorCode;
import com.lxp.aplus.course.presentation.response.LectureResourceResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lectures")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureResource> lectureResources = new ArrayList<>();

    @Column(name = "total_duration_seconds")
    private Integer totalDurationSeconds;

    @Column (nullable = false)
    private String title;

    @Column (name = "is_preview", nullable = false)
    private boolean isPreview;

    @Column(name = "order_index", nullable = false)
    @Min(1)
    private int orderIndex;

    private Lecture(Section section, String title, Integer totalDurationSeconds, boolean isPreview, int orderIndex) {
        this.section = section;
        this.title = title;
        this.totalDurationSeconds = totalDurationSeconds;
        this.isPreview = isPreview;
        this.orderIndex = orderIndex;
        this.lectureResources = new ArrayList<>();
    }

    public static Lecture create(Section section, String title, Integer totalDurationSeconds, boolean isPreview, int orderIndex, CreateLectureResourceSpec resourceSpec) {
        if (resourceSpec == null) {
            throw new BusinessException(LectureErrorCode.LECTURE_RESOURCE_REQUIRED);
        }

        Lecture lecture = new Lecture(section, title, totalDurationSeconds, isPreview, orderIndex);
        LectureResource resource = LectureResource.create(lecture, resourceSpec.isDownloadable());
        lecture.lectureResources.add(resource);

        return lecture;
    }

    public void update(String title, Integer totalDurationSeconds, boolean isPreview, int orderIndex, UpdateLectureResourceSpec resourceSpec) {
        if (resourceSpec == null) {
            throw new BusinessException(LectureErrorCode.LECTURE_RESOURCE_REQUIRED);
        }

        this.title = title;
        this.totalDurationSeconds = totalDurationSeconds;
        this.isPreview = isPreview;
        this.orderIndex = orderIndex;

        this.lectureResources.clear();
        LectureResource resource = LectureResource.create(this, resourceSpec.isDownloadable());
        this.lectureResources.add(resource);
    }
}

package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
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

    @Column(name = "total_duration_seconds", nullable = false)
    private Integer totalDurationSeconds;

    @Column (nullable = false)
    private String title;

    @Column (name = "is_preview", nullable = false)
    private boolean isPreview;

    @Column(name = "order_index", nullable = false)
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
        Lecture lecture = new Lecture(section, title, totalDurationSeconds, isPreview, orderIndex);
        if (resourceSpec != null) {
            LectureResource resource = LectureResource.create(lecture, resourceSpec.isDownloadable());
            lecture.lectureResources.add(resource);
        }
        return lecture;
    }

    public void update(String title, Integer totalDurationSeconds, boolean isPreview, int orderIndex, UpdateLectureResourceSpec resourceSpec) {
        this.title = title;
        this.totalDurationSeconds = totalDurationSeconds;
        this.isPreview = isPreview;
        this.orderIndex = orderIndex;

        if (resourceSpec != null) {
            this.lectureResources.clear();
            LectureResource resource = LectureResource.create(this, resourceSpec.isDownloadable());
            this.lectureResources.add(resource);
        }
    }
}

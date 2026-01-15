package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.BatchSize;

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

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureResourceV2> lectureResources = new ArrayList<>();

    @Column(name = "total_duration_seconds")
    private Integer totalDurationSeconds;

    @Column (nullable = false)
    private String title;

    @Column (name = "is_preview", nullable = false)
    private boolean isPreview;

    @Column(name = "order_index", nullable = false)
    @Min(1)
    private int orderIndex;

    private Lecture(Section section, String title, LectureResourceV2 resource, boolean isPreview, int orderIndex) {
        this.section = section;
        this.title = title;
        this.isPreview = isPreview;
        this.orderIndex = orderIndex;
        this.lectureResources.add(resource);
        this.totalDurationSeconds = resource.getVideoDuration().getDuration();
    }

    public static Lecture create(Section section, String title, LectureResourceV2 resource, boolean isPreview, int orderIndex) {
        Lecture lecture = new Lecture(section, title, resource, isPreview, orderIndex);
        lecture.addResource(resource);

        return lecture;
    }

    public void addResource(LectureResourceV2 resource) {
        this.lectureResources.add(resource);
        resource.assignToLecture(this);
    }

    public void update(String title, boolean isPreview, int orderIndex) {
        this.title = title;
        this.isPreview = isPreview;
        this.orderIndex = orderIndex;
    }

    // TODO: 업데이트 로직 구현 필요 
//    public void update(String title, Integer totalDurationSeconds, boolean isPreview, int orderIndex, boolean isDownloadable, String fileKey, String fileUrl, String originFileName) {
//        this.title = title;
//        this.totalDurationSeconds = totalDurationSeconds;
//        this.isPreview = isPreview;
//        this.orderIndex = orderIndex;
//
//        this.lectureResources.clear();
//        LectureResource resource = LectureResource.create(this, isDownloadable, fileKey, fileUrl, originFileName);
//        this.lectureResources.add(resource);
//    }

    public void validateHasResource() {
        if (this.lectureResources == null || this.lectureResources.isEmpty()) {
            throw new BusinessException(CourseErrorCode.COURSE_CONTENT_EMPTY);
        }
    }
}

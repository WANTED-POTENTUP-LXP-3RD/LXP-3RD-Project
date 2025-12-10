package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String title;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    public Lecture addLecture(CreateLectureSpec spec) {
        Lecture lecture = Lecture.create(this, spec.title(), spec.totalDurationSeconds(), spec.isPreview(), spec.orderIndex(), spec.resource());
        this.lectures.add(lecture);
        return lecture;
    }

    public Lecture updateLecture(UpdateLectureSpec spec) {
        Lecture lecture = lectures.stream()
                .filter(l -> l.getId().equals(spec.lectureId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(LectureErrorCode.LECTURE_NOT_FOUND));
        lecture.update(spec.title(), spec.totalDurationSeconds(), spec.isPreview(), spec.orderIndex(), spec.resource());
        return lecture;
    }

    public void deleteLecture(Long lectureId) {
        Lecture target = lectures.stream()
                .filter(l -> l.getId().equals(lectureId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(LectureErrorCode.LECTURE_NOT_FOUND));
        lectures.remove(target);
    }

    public Lecture readLecture (Long lectureId) {
        return lectures.stream()
                .filter(l -> l.getId().equals(lectureId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(LectureErrorCode.LECTURE_NOT_FOUND));
    }

    public boolean hasLecture(Long lectureId) {
        return lectures.stream()
                .anyMatch(l -> l.getId().equals(lectureId));
    }
}

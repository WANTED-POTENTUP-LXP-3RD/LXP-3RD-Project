package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureErrorCode;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    public static Section createSection(Course course, String title, int orderIndex) {
        return Section.builder()
                .course(course)
                .title(title)
                .orderIndex(orderIndex)
                .build();
    }

    public void update(String title, Integer orderIndex) {
        if (title != null) this.title = title;
        if (orderIndex != null) this.orderIndex = orderIndex;
    }
}

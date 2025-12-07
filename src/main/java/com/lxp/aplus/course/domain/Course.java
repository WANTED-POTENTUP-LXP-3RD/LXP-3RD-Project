package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import com.lxp.aplus.course.application.command.CourseUpdateCommand;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Course extends BaseAggregateRoot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long instructorId;

    @Column
    private Long categoryId;

    @Column
    private String title;

    @Column
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String thumbnailUrl;

    @Column
    private Integer price;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus courseStatus = CourseStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column
    private CourseLevel courseLevel;

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public static Course from(Long instructorId) {
        return Course.builder()
                .instructorId(instructorId)
                .build();
    }

    public void updateCourseInfo(CourseUpdateCommand request) {
        this.title = request.title();
        this.summary = request.summary();
        this.description = request.description();
        this.categoryId = request.categoryId();
        this.courseLevel = request.courseLevel();
        this.thumbnailUrl = request.thumbnailUrl();
        this.price = request.price();
    }
}

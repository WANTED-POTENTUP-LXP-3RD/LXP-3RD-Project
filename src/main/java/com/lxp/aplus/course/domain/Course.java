package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.application.command.CourseCreateCommand;
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

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String summary;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private int price;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus courseStatus = CourseStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseLevel courseLevel;

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public static Course createDraftCourse(Long instructorId, CourseCreateCommand command) {
        return Course.builder()
                .instructorId(instructorId)
                .categoryId(command.categoryId())
                .title(command.title())
                .summary(command.summary())
                .description(command.description())
                .thumbnailUrl(command.thumbnailUrl())
                .price(command.price())
                .courseLevel(command.courseLevel())
                .build();
    }

    public void updateCourseInfo(CourseUpdateCommand command) {
        if (command.title() != null) this.title = command.title();
        if (command.summary() != null) this.summary = command.summary();
        if (command.description() != null) this.description = command.description();
        if (command.categoryId() != null) this.categoryId = command.categoryId();
        if (command.thumbnailUrl() != null) this.thumbnailUrl = command.thumbnailUrl();
        if (command.price() != null) this.price = command.price();
        if (command.courseLevel() != null) this.courseLevel = command.courseLevel();
    }

    public void validateOwner(Long userId) {
        if (!this.instructorId.equals(userId)) {
            throw new BusinessException(CourseErrorCode.COURSE_ACCESS_DENIED);
        }
    }
}

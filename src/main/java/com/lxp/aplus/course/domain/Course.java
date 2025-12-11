package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import com.lxp.aplus.common.error.code.SectionErrorCode;
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
import java.util.Objects;

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

    public void updateCourse(CourseUpdateCommand command) {
        if (command.title() != null) {
            if (command.title().isBlank()) {
                throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT); // 또는 적절한 CourseErrorCode
            }
            this.title = command.title();
        }

        if (command.summary() != null) {
            if (command.summary().isBlank()) {
                throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT);
            }
            this.summary = command.summary();
        }

        if (command.description() != null) {
            if (command.description().isBlank()) {
                throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT);
            }
            this.description = command.description();
        }

        if (command.categoryId() != null) {
            this.categoryId = command.categoryId();
        }

        if (command.thumbnailUrl() != null) {
            if (command.thumbnailUrl().isBlank()) {
                throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT);
            }
            this.thumbnailUrl = command.thumbnailUrl();
        }

        if (command.price() != null) {
            if (command.price() < 0) {
                throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT);
            }
            this.price = command.price();
        }

        if (command.courseLevel() != null) {
            this.courseLevel = command.courseLevel();
        }
    }

    public void deleteCourse() {
        validateEditable();
        this.courseStatus = CourseStatus.DELETED;
    }

    public void addSection(String title, int orderIndex) {
        validateEditable();
        validateSectionOrder(orderIndex);

        Section newSection = Section.createSection(this, title, orderIndex);
        this.sections.add(newSection);
    }

    public Section updateSection(Long sectionId, String title, Integer orderIndex) {
        validateEditable();

        Section targetSection = this.sections.stream()
                .filter(section -> Objects.equals(section.getId(), sectionId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(SectionErrorCode.SECTION_NOT_FOUND));

        if (orderIndex != null && targetSection.getOrderIndex() != orderIndex) {
            validateSectionOrder(orderIndex);
        }

        targetSection.updateSection(title, orderIndex);
        return targetSection;
    }

    public void deleteSection(Long sectionId) {
        validateEditable();

        Section targetSection = this.sections.stream()
                .filter(section -> Objects.equals(section.getId(), sectionId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(SectionErrorCode.SECTION_NOT_FOUND));

        this.sections.remove(targetSection);
    }

    public void validateOwner(Long userId) {
        if (!this.instructorId.equals(userId)) {
            throw new BusinessException(GlobalErrorCode.VALIDATION_ERROR);
        }
    }

    private void validateSectionOrder(int orderIndex) {
        boolean isOrderIndexDuplicated = this.sections.stream()
                .anyMatch(section -> section.getOrderIndex() == orderIndex);

        if (isOrderIndexDuplicated) {
            throw new BusinessException(SectionErrorCode.SECTION_ORDER_DUPLICATED);
        }
    }

    private void validateEditable() {
        if (this.courseStatus == CourseStatus.PUBLISHED) {
            throw new BusinessException(CourseErrorCode.CANNOT_MODIFY_PUBLISHED_COURSE);
        }
    }
}

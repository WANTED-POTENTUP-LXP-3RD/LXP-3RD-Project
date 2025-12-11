package com.lxp.aplus.progress.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.enrollment.domain.Enrollment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "progresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Progress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_resource_id", nullable = false)
    private LectureResource lectureResource;

    @Column(nullable = false)
    private Integer watchedDuration = 0;

    @Column(nullable = false)
    private boolean isCompleted = false;

    private LocalDateTime lastWatchedAt;

    @Builder
    public Progress(Enrollment enrollment, LectureResource lectureResource, Integer watchedDuration, boolean isCompleted, LocalDateTime lastWatchedAt) {
        this.enrollment = enrollment;
        this.lectureResource = lectureResource;
        this.watchedDuration = watchedDuration;
        this.isCompleted = isCompleted;
        this.lastWatchedAt = lastWatchedAt;
    }

    public void updateProgress(Integer watchedDuration, boolean isCompleted) {
        if (this.enrollment.isExpired()) {
            throw new BusinessException(ProgressErrorCode.CANNOT_UPDATE_EXPIRED_ENROLLMENT);
        }
        this.watchedDuration = watchedDuration;
        this.isCompleted = isCompleted;
        this.lastWatchedAt = LocalDateTime.now();
    }
}

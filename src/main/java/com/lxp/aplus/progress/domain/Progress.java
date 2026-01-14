package com.lxp.aplus.progress.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ProgressErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "progresses", indexes = {
        @Index(name = "idx_enrollment_id", columnList = "enrollment_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Progress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @Column(nullable = false)
    private Long enrollmentId;

    @Column(nullable = false)
    private Long lectureResourceId;

    @Column(nullable = false)
    private Integer watchedDuration = 0;

    @Column(nullable = false)
    private boolean completed = false;

    private LocalDateTime lastWatchedAt;

    private Progress(Long enrollmentId, Long lectureResourceId) {
        this.enrollmentId = enrollmentId;
        this.lectureResourceId = lectureResourceId;
        this.watchedDuration = 0;
        this.completed = false;
        this.lastWatchedAt = null;
    }

    public static Progress of(Long enrollmentId, Long lectureResourceId) {
        Objects.requireNonNull(enrollmentId, "수강 ID(enrollmentId)는 null일 수 없습니다.");
        Objects.requireNonNull(lectureResourceId, "강의 리소스 ID(lectureResourceId)는 null일 수 없습니다.");

        return new Progress(enrollmentId, lectureResourceId);
    }

    public void updateProgress(Integer newWatchedDuration, int totalLectureDuration) {
        if (newWatchedDuration > totalLectureDuration) {
            throw new BusinessException(ProgressErrorCode.WATCHED_DURATION_EXCEEDS_TOTAL);
        }

        this.watchedDuration = newWatchedDuration;
        this.lastWatchedAt = LocalDateTime.now();

        if (newWatchedDuration >= totalLectureDuration) {
            this.completed = true;
        }
    }
}

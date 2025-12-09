package com.lxp.aplus.enrollment.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.common.domain.BaseAggregateRoot;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_enrollment_student_course",
                columnNames = {"studentId", "courseId"}
            )
        }
)
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment extends BaseAggregateRoot {

    private static final int MIN_PROGRESS_RATE = 0;
    private static final int MAX_PROGRESS_RATE = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(nullable = false)
    @Builder.Default
    private int progressRate = MIN_PROGRESS_RATE;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public static Enrollment of(Long studentId, Long courseId, LocalDateTime expiredAt) {
        if (Objects.isNull(studentId) || Objects.isNull(courseId) || Objects.isNull(expiredAt)) {
            throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT);
        }

        return Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .expiredAt(expiredAt)
                .build();
    }

    public void cancel() {
        if (this.status == EnrollmentStatus.COMPLETED) {
            throw new BusinessException(EnrollmentErrorCode.CANNOT_CANCEL_COMPLETED_ENROLLMENT);
        }
        if (this.status == EnrollmentStatus.CANCELED) {
            throw new BusinessException(EnrollmentErrorCode.ALREADY_CANCELLED_ENROLLMENT);
        }
        if (isExpired()) {
            throw new BusinessException(EnrollmentErrorCode.CANNOT_CANCEL_EXPIRED_ENROLLMENT);
        }
        this.status = EnrollmentStatus.CANCELED;
    }

    public void updateProgress(int newProgressRate) {
        if (this.status != EnrollmentStatus.ENROLLED) {
            throw new BusinessException(EnrollmentErrorCode.CANNOT_UPDATE_PROGRESS_FOR_NON_ENROLLED);
        }
        if (isExpired()) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_EXPIRED_PROGRESS_UPDATE_DENIED);
        }
        if (newProgressRate < MIN_PROGRESS_RATE || newProgressRate > MAX_PROGRESS_RATE) {
            throw new BusinessException(EnrollmentErrorCode.INVALID_PROGRESS_RATE);
        }

        this.progressRate = newProgressRate;

        if (this.progressRate == MAX_PROGRESS_RATE) {
            this.complete();
        }
    }

    private void complete() {
        this.progressRate = MAX_PROGRESS_RATE;
        this.status = EnrollmentStatus.COMPLETED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiredAt);
    }
}

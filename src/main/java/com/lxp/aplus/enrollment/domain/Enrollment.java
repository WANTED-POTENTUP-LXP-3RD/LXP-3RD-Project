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

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiredAt);
    }
    public void completeEnrollment() {
        if (this.status == EnrollmentStatus.ENROLLED) {
            this.status = EnrollmentStatus.COMPLETED;
        }
    }

    public void validateOwner(Long studentId) {
        if (!this.studentId.equals(studentId)) {
            throw new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS);
        }
    }
}

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

    private static final long DEFAULT_EXPIRATION_YEARS = 2L;
    private static final int PERCENTAGE_MULTIPLIER = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long courseId;

    @Column(name = "order_item_id")
    private Long orderItemId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public static Enrollment create(Long studentId, Long courseId, Long orderItemId) {
        if (Objects.isNull(studentId) || Objects.isNull(courseId)) {
            throw new BusinessException(GlobalErrorCode.INVALID_ARGUMENT);
        }

        return Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .orderItemId(orderItemId)
                .expiredAt(LocalDateTime.now().plusYears(DEFAULT_EXPIRATION_YEARS))
                .build();
    }

    public void cancel(LocalDateTime currentTime) {
        if (this.status == EnrollmentStatus.COMPLETED) {
            throw new BusinessException(EnrollmentErrorCode.CANNOT_CANCEL_COMPLETED_ENROLLMENT);
        }
        if (this.status == EnrollmentStatus.CANCELED) {
            throw new BusinessException(EnrollmentErrorCode.ALREADY_CANCELLED_ENROLLMENT);
        }
        if (isExpired(currentTime)) {
            throw new BusinessException(EnrollmentErrorCode.CANNOT_CANCEL_EXPIRED_ENROLLMENT);
        }
        this.status = EnrollmentStatus.CANCELED;
    }

    /**
     * 진도율을 계산하는 도메인 정책입니다.
     * @param completedResourceCount 완료된 리소스 수
     * @param totalResourceCount 전체 리소스 수
     * @return 진도율 (0-100)
     */
    public int calculateProgressRate(long completedResourceCount, long totalResourceCount) {
        if (totalResourceCount == 0) {
            return 0;
        }
        return (int) ((double) completedResourceCount / totalResourceCount * PERCENTAGE_MULTIPLIER);
    }

    public boolean isExpired(LocalDateTime currentTime) {
        return currentTime.isAfter(this.expiredAt);
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

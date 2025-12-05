package com.lxp.aplus.enrollment.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Column(nullable = false)
    private Integer progressRate;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Enrollment(Long studentId, Long courseId, EnrollmentStatus status, Integer progressRate, LocalDateTime expiredAt) {
        Assert.notNull(studentId, "studentId는 null일 수 없습니다.");
        Assert.notNull(courseId, "courseId는 null일 수 없습니다.");
        Assert.notNull(status, "status는 null일 수 없습니다.");

        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
        this.progressRate = progressRate;
        this.expiredAt = expiredAt;
    }

    public static Enrollment enroll(Long studentId, Long courseId, LocalDateTime expiredAt) {
        Assert.notNull(expiredAt, "expiredAt은 null일 수 없습니다.");

        return Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .status(EnrollmentStatus.ENROLLED)
                .progressRate(0)
                .expiredAt(expiredAt)
                .build();
    }

    public void cancel() {
        if (this.status == EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException("이미 수료한 강의는 취소할 수 없습니다.");
        }
        this.status = EnrollmentStatus.CANCELED;
    }

    public void updateProgress(int newProgressRate) {
        if (this.status != EnrollmentStatus.ENROLLED) {
            throw new IllegalStateException("수강 중인 강의만 진도율을 업데이트할 수 있습니다.");
        }
        if (newProgressRate < 0 || newProgressRate > 100) {
            throw new IllegalArgumentException("진도율은 0~100 사이여야 합니다.");
        }

        this.progressRate = newProgressRate;

        if (this.progressRate == 100) {
            this.complete();
        }
    }

    public void complete() {
        this.progressRate = 100;
        this.status = EnrollmentStatus.COMPLETED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiredAt);
    }
}

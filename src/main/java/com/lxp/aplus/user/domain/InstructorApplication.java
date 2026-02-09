package com.lxp.aplus.user.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Entity
@Table(name = "instructor_applications")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class InstructorApplication extends BaseAggregateRoot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InstructorApplicationStatus status = InstructorApplicationStatus.PENDING;

    public static InstructorApplication create(Long userId) {
        return InstructorApplication.builder()
                .userId(userId)
                .build();
    }

    public void approve() {
        this.status = InstructorApplicationStatus.APPROVED;
    }

    public void reject() {
        this.status = InstructorApplicationStatus.REJECTED;
    }
}

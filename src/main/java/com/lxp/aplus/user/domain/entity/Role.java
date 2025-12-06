package com.lxp.aplus.user.domain.entity;

import com.lxp.aplus.user.domain.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "role_type"})
        })
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", updatable = false, insertable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, updatable = false)
    private RoleType roleType;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected static Role of(Long userId, RoleType roleType) {
        return Role.builder()
                .userId(userId)
                .roleType(roleType)
                .build();
    }

    // TODO : 권한 회수 정책 검토 필요
    //  권한 부여 이력 확인을 위해 soft delete정책을 적용하였으나
    //  예를들어 강사 권한을 회수한 이후 다시 재부여를 했을 경우 unique 제약조건에 걸리게 됨.
    //  해당케이스를 고려해서 unique 제약조건을 해제하거나, 권한 회수 정책을 변경할 필요가 있음.
    protected void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}


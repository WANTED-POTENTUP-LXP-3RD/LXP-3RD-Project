package com.lxp.aplus.user.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.UserErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = {"email"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseAggregateRoot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "nick_name")
    private String nickName;

    @Column
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    // TODO : 암호화 필요
    @Column
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static User of(String name, String nickName, String email, String password, String phoneNumber) {
        User user = User.builder()
                .name(name)
                .nickName(nickName)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .build();

        user.addRole(RoleType.STUDENT);
        return user;
    }

    public void addRole(RoleType roleType) {
        // 이미 존재하는 역할인지 확인
        boolean alreadyExists = this.roles.stream()
                .anyMatch(role -> role.getRoleType() == roleType && role.getDeletedAt() == null);
        
        if (alreadyExists) {
            throw new BusinessException(UserErrorCode.ROLE_ALREADY_EXISTS);
        }
        
        Role role = Role.of(this.id, roleType);
        this.roles.add(role);
    }

    public void updateUserInfo(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }

    /**
     * 사용자 탈퇴 처리
     * 상태를 WITHDRAWN으로 변경
     */
    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;
    }

    /**
     * 휴면 해제 처리
     * 상태를 INACTIVE에서 ACTIVE로 변경
     */
    public void activate() {
        if (this.status != UserStatus.INACTIVE)
            throw new BusinessException(UserErrorCode.INVALID_STATUS_TRANSITION);

        this.status = UserStatus.ACTIVE;
    }

    /**
     * 상태 변경 (내부용)
     * 내부 비즈니스 로직에서만 사용
     */
    void updateStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * PENDING 상태에서 ACTIVE로 활성화 (임시용)
     *
     * TODO: 프로덕션에서는 이메일 인증 등 추가 검증 후 활성화해야 함
     */
    public void activateFromPending() {
        if (this.status == UserStatus.PENDING) {
            this.status = UserStatus.ACTIVE;
        }
    }

    // TODO : 암호화 필요
    public void changePassword(String password) {
        this.password = password;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        // 연관된 Role들도 모두 soft deleteCourse
        this.roles.stream()
                .filter(role -> role.getDeletedAt() == null) // 아직 삭제되지 않은 Role만
                .forEach(Role::delete);
    }

}

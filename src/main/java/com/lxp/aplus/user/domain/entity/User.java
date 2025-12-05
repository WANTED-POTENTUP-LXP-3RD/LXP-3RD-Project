package com.lxp.aplus.user.domain.entity;

import com.lxp.aplus.user.domain.enums.RoleType;
import com.lxp.aplus.user.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void addRole(RoleType roleType) {
        Role role = Role.builder()
                .userId(this.id)
                .roleType(roleType)
                .build();
        this.roles.add(role);
    }

    public void updateUserInfo(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }

    // TODO : 상태값에 따른 비즈니스 로직은 서비스 레이어에서 처리
    public void updateStatus(UserStatus status) {
        this.status = status;
    }

    public void changePassword(String password) {
        // TODO : 암호화 필요
        this.password = password;
    }

    public static User createUser(String name, String email, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        user.addRole(RoleType.STUDENT);
        return user;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

}

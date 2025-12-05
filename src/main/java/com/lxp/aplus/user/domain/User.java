package com.lxp.aplus.user.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import jakarta.persistence.*;
import lombok.*;
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

    public static User of(String name, String email, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        user.addRole(RoleType.STUDENT);
        return user;
    }

    public void addRole(RoleType roleType) {
        Role role = Role.of(this.id, roleType);
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

    // TODO : 암호화 필요
    public void changePassword(String password) {
        this.password = password;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

}

/*
package com.lxp.aplus.user.domain.repository;

import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import com.lxp.aplus.user.infrastructure.persistence.UserRepositoryImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserRepositoryImpl.class)
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("User를 저장하고 조회할 수 있다")
    void saveAndFindById() {
        // given
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");

        // when - save
        User savedUser = userRepository.save(user);

        // when - findById
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isNotNull();
        assertThat(foundUser.get().getName()).isEqualTo("홍길동");
        assertThat(foundUser.get().getEmail()).isEqualTo("hong@example.com");
        assertThat(foundUser.get().getPassword()).isEqualTo("password123");
        assertThat(foundUser.get().getRoles()).hasSize(1);
        assertThat(foundUser.get().getRoles().get(0).getRoleType()).isEqualTo(RoleType.STUDENT);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 Optional.empty()를 반환한다")
    void findById_NotFound() {
        // when
        Optional<User> foundUser = userRepository.findById(999L);

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("User를 저장하면 Role도 함께 저장된다")
    void save_WithRole() {
        // given
        User user = User.of("김영희", "kim", "kim@example.com", "password123", "010-9876-5432");
        user.addRole(RoleType.INSTRUCTOR); // 추가 역할

        // when
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRoles()).hasSize(2);
        assertThat(foundUser.get().getRoles())
                .extracting("roleType")
                .containsExactlyInAnyOrder(RoleType.STUDENT, RoleType.INSTRUCTOR);
    }

    @Test
    @DisplayName("User의 닉네임과 이메일을 업데이트할 수 있다")
    void updateUserInfo() {
        // given
        User user = User.of("이철수", "lee",  "lee@example.com", "password123", "010-1111-2222");
        User savedUser = userRepository.save(user);
        LocalDateTime beforeUpdate = savedUser.getUpdatedAt();

        // when
        savedUser.updateUserInfo("새닉네임", "newemail@example.com");
        User updatedUser = userRepository.save(savedUser);
        Optional<User> foundUser = userRepository.findById(updatedUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getNickName()).isEqualTo("새닉네임");
        assertThat(foundUser.get().getEmail()).isEqualTo("newemail@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("이철수"); // name은 변경되지 않음
        assertThat(foundUser.get().getUpdatedAt()).isAfter(beforeUpdate); // updatedAt 자동 업데이트 확인
    }

    @Test
    @DisplayName("User의 비밀번호를 변경할 수 있다")
    void changePassword() {
        // given
        User user = User.of("박민수", "park", "park@example.com", "oldPassword123", "010-3333-4444");
        User savedUser = userRepository.save(user);

        // when
        savedUser.changePassword("newPassword456");
        User updatedUser = userRepository.save(savedUser);
        Optional<User> foundUser = userRepository.findById(updatedUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getPassword()).isEqualTo("newPassword456");
        assertThat(foundUser.get().getEmail()).isEqualTo("park@example.com"); // 다른 필드는 변경되지 않음
    }

    @Test
    @DisplayName("User를 삭제할 수 있다 (Soft Delete)")
    void delete() {
        // given
        User user = User.of("최지영", "choi", "choi@example.com", "password123", "010-5555-6666");
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getDeletedAt()).isNull();

        // when
        savedUser.delete();
        User deletedUser = userRepository.save(savedUser);
        Optional<User> foundUser = userRepository.findById(deletedUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getDeletedAt()).isNotNull();
        assertThat(foundUser.get().getDeletedAt()).isBefore(LocalDateTime.now().plusSeconds(1)); // 현재 시간과 비슷한지 확인
    }

    @Test
    @DisplayName("User에 역할을 추가할 수 있다")
    void addRole() {
        // given
        User user = User.of("강사1","gang",  "instructor1@example.com", "password123", "010-7777-8888");
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getRoles()).hasSize(1); // 기본 STUDENT 역할

        // when
        savedUser.addRole(RoleType.INSTRUCTOR);
        savedUser.addRole(RoleType.ADMIN);
        User updatedUser = userRepository.save(savedUser);
        Optional<User> foundUser = userRepository.findById(updatedUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRoles()).hasSize(3);
        assertThat(foundUser.get().getRoles())
                .extracting("roleType")
                .containsExactlyInAnyOrder(RoleType.STUDENT, RoleType.INSTRUCTOR, RoleType.ADMIN);
    }

    @Test
    @DisplayName("User 생성 시 기본적으로 STUDENT 역할이 설정된다")
    void createUser_WithDefaultRole() {
        // when
        User user = User.of("테스트유저", "test", "test@example.com", "password123", "010-9999-0000");

        // then
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getRoles().get(0).getRoleType()).isEqualTo(RoleType.STUDENT);
        assertThat(user.getName()).isEqualTo("테스트유저");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
    }

}

*/

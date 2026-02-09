package com.lxp.aplus.user.application.usecase;

import com.lxp.aplus.user.application.dto.UserResponse;
import com.lxp.aplus.user.domain.RoleType;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryUseCase 테스트")
class UserQueryUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryUseCase userQueryUseCase;

    @Test
    @DisplayName("ID로 User를 조회할 수 있다")
    void findUserById() {
        // given
        Long userId = 1L;
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        Optional<UserResponse> response = userQueryUseCase.findUserById(userId);

        // then
        assertThat(response).isPresent();
        assertThat(response.get().name()).isEqualTo("홍길동");
        assertThat(response.get().nickname()).isEqualTo("hong");
        assertThat(response.get().email()).isEqualTo("hong@example.com");
        assertThat(response.get().phonenumber()).isEqualTo("010-1234-5678");
        assertThat(response.get().status()).isEqualTo(UserStatus.PENDING);
        assertThat(response.get().roles()).hasSize(1);
        assertThat(response.get().roles().get(0)).isEqualTo(RoleType.STUDENT);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 User 조회 시 Optional.empty()를 반환한다")
    void findUserById_NotFound() {
        // given
        Long notExistUserId = 999L;

        when(userRepository.findById(notExistUserId)).thenReturn(Optional.empty());
        // when
        Optional<UserResponse> response = userQueryUseCase.findUserById(notExistUserId);

        // then
        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("ID로 User와 Role을 함께 조회할 수 있다")
    void findUserWithRolesById() {
        // given
        Long userId = 1L;
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        user.addRole(RoleType.INSTRUCTOR);
        user.addRole(RoleType.ADMIN);
        
        when(userRepository.findUserWithRolesById(userId)).thenReturn(Optional.of(user));

        // when
        Optional<UserResponse> response = userQueryUseCase.findUserWithRolesById(userId);

        // then
        assertThat(response).isPresent();
        assertThat(response.get().roles()).hasSize(3);
        assertThat(response.get().roles()).containsExactlyInAnyOrder(
                RoleType.STUDENT,
                RoleType.INSTRUCTOR,
                RoleType.ADMIN
        );
        verify(userRepository, times(1)).findUserWithRolesById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 User와 Role 조회 시 Optional.empty()를 반환한다")
    void findUserByIdWithRoles_NotFound() {
        // when
        Optional<UserResponse> response = userQueryUseCase.findUserWithRolesById(999L);

        // then
        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("User 존재 여부를 확인할 수 있다")
    void existsById() {
        // given
        Long userId = 1L;
        Long notExistUserId = 999L;
        
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.existsById(notExistUserId)).thenReturn(false);

        // when
        boolean exists = userQueryUseCase.existsById(userId);
        boolean notExists = userQueryUseCase.existsById(notExistUserId);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).existsById(notExistUserId);
    }
}


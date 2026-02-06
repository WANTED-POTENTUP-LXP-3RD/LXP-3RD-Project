package com.lxp.aplus.user.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.user.domain.RoleType;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserStatus;
import com.lxp.aplus.common.error.code.UserErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandUseCase 테스트")
class UserCommandUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserQueryUseCase userQueryUseCase;

    @InjectMocks
    private UserCommandUseCase userCommandUseCase;

    @Test
    @DisplayName("User를 생성할 수 있다")
    void createUser() {
        // given
        CreateUserRequest request = new CreateUserRequest(
                "홍길동",
                "hong",
                "hong@example.com",
                "password123",
                "010-1234-5678"
        );
        
        User savedUser = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserResponse response = userCommandUseCase.createUser(request);

        // then
        assertThat(response.name()).isEqualTo("홍길동");
        assertThat(response.nickname()).isEqualTo("hong");
        assertThat(response.email()).isEqualTo("hong@example.com");
        assertThat(response.phonenumber()).isEqualTo("010-1234-5678");
        assertThat(response.status()).isEqualTo(UserStatus.PENDING);
        assertThat(response.roles()).hasSize(1);
        assertThat(response.roles().get(0)).isEqualTo(RoleType.STUDENT);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("User 정보를 업데이트할 수 있다")
    void updateUserInfo() {
        // given
        Long userId = 1L;
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        
        UpdateUserInfoRequest request = new UpdateUserInfoRequest(
                userId,
                "hongUpdated",
                "hong.updated@example.com"
        );
        
        when(userQueryUseCase.findByIdWithRoles(userId)).thenReturn(Optional.of(user));

        // when
        UserResponse response = userCommandUseCase.updateUserInfo(request);

        // then
        assertThat(response.nickname()).isEqualTo("hongUpdated");
        assertThat(response.email()).isEqualTo("hong.updated@example.com");
        assertThat(response.name()).isEqualTo("홍길동"); // 변경되지 않음
        verify(userQueryUseCase, times(1)).findByIdWithRoles(userId);
    }

    @Test
    @DisplayName("존재하지 않는 User 정보 업데이트 시 예외가 발생한다")
    void updateUserInfo_NotFound() {
        // given
        UpdateUserInfoRequest request = new UpdateUserInfoRequest(
                999L,
                "hongUpdated",
                "hong.updated@example.com"
        );
        
        when(userQueryUseCase.findByIdWithRoles(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userCommandUseCase.updateUserInfo(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("User에 강사 Role을 추가할 수 있다")
    void addRole() {
        // given
        Long userId = 1L;
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        
        AddRoleRequest request = new AddRoleRequest(userId, RoleType.ADMIN);
        
        when(userQueryUseCase.findByIdWithRoles(userId)).thenReturn(Optional.of(user));

        // when
        UserResponse response = userCommandUseCase.addInstructorRole(userId);

        // then
        assertThat(response.roles()).hasSize(2);
        assertThat(response.roles()).contains(RoleType.STUDENT, RoleType.ADMIN);
        verify(userQueryUseCase, times(1)).findByIdWithRoles(userId);
    }

    @Test
    @DisplayName("이미 존재하는 Role 추가 시 예외가 발생한다")
    void addRole_AlreadyExists() {
        // given
        Long userId = 1L;
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        user.addRole(RoleType.ADMIN);
        
        AddRoleRequest request = new AddRoleRequest(userId, RoleType.ADMIN);
        
        when(userQueryUseCase.findByIdWithRoles(userId))
                .thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userCommandUseCase.addInstructorRole(userId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.ROLE_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("User를 탈퇴 처리할 수 있다")
    void withdrawUser() {
        // given
        Long userId = 1L;
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        
        WithdrawUserRequest request = new WithdrawUserRequest(userId);
        
        when(userQueryUseCase.findByIdWithRoles(userId)).thenReturn(Optional.of(user));

        // when
        UserResponse response = userCommandUseCase.withdrawUser(request);

        // then
        assertThat(response.status()).isEqualTo(UserStatus.WITHDRAWN);
        verify(userQueryUseCase, times(1)).findByIdWithRoles(userId);
    }

    @Test
    @DisplayName("User를 활성화할 수 있다")
    void activateUser() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .name("홍길동")
                .nickName("hong")
                .email("hong@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .status(UserStatus.INACTIVE)
                .build();
        
        ActivateUserRequest request = new ActivateUserRequest(userId);
        
        when(userQueryUseCase.findByIdWithRoles(userId)).thenReturn(Optional.of(user));

        // when
        UserResponse response = userCommandUseCase.activateUser(request);

        // then
        assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
        verify(userQueryUseCase, times(1)).findByIdWithRoles(userId);
    }

    @Test
    @DisplayName("INACTIVE가 아닌 상태에서 활성화 시 예외가 발생한다")
    void activateUser_InvalidStatus() {
        // given
        Long userId = 1L;
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        
        ActivateUserRequest request = new ActivateUserRequest(userId);
        
        when(userQueryUseCase.findByIdWithRoles(userId)).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userCommandUseCase.activateUser(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.INVALID_STATUS_TRANSITION);
    }

    @Test
    @DisplayName("User 비밀번호를 변경할 수 있다")
    void changePassword() {
        // given
        Long userId = 1L;
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        
        ChangePasswordRequest request = new ChangePasswordRequest(userId, "newPassword123");
        
        when(userQueryUseCase.findById(userId)).thenReturn(Optional.of(user));

        // when
        userCommandUseCase.changePassword(request);

        // then
        verify(userQueryUseCase, times(1)).findById(userId);
    }

    @Test
    @DisplayName("User를 삭제할 수 있다")
    void deleteUser() {
        // given
        Long userId = 1L;
        User user = User.of("홍길동", "hong", "hong@example.com", "password123", "010-1234-5678");
        user.addRole(RoleType.ADMIN);
        
        DeleteUserRequest request = new DeleteUserRequest(userId);
        
        when(userQueryUseCase.findByIdWithRoles(userId)).thenReturn(Optional.of(user));

        // when
        userCommandUseCase.deleteUser(request);

        // then
        verify(userQueryUseCase, times(1)).findByIdWithRoles(userId);
    }
}


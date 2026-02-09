package com.lxp.aplus.user.application.service;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.UserErrorCode;
import com.lxp.aplus.common.security.CustomPasswordEncoder;
import com.lxp.aplus.user.application.dto.AuthUser;
import com.lxp.aplus.user.application.dto.request.ActivateUserRequest;
import com.lxp.aplus.user.application.dto.request.ChangePasswordRequest;
import com.lxp.aplus.user.application.dto.request.CreateUserRequest;
import com.lxp.aplus.user.application.dto.request.DeleteUserRequest;
import com.lxp.aplus.user.application.dto.request.UpdateUserInfoRequest;
import com.lxp.aplus.user.application.dto.request.WithdrawUserRequest;
import com.lxp.aplus.user.application.dto.response.UserResponse;
import com.lxp.aplus.user.application.port.in.UserCommandUseCase;
import com.lxp.aplus.user.application.port.in.UserQueryUseCase;
import com.lxp.aplus.user.domain.RoleType;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.application.port.out.UserRepository;
import com.lxp.aplus.user.application.port.out.InstructorApplicationRepository;
import com.lxp.aplus.user.domain.InstructorApplication;
import com.lxp.aplus.user.domain.InstructorApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User 도메인의 통합 서비스
 * UserCommandUseCase와 UserQueryUseCase를 모두 구현
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserCommandUseCase, UserQueryUseCase {
    private final UserRepository userRepository;
    private final InstructorApplicationRepository instructorApplicationRepository;
    private final CustomPasswordEncoder customPasswordEncoder;

    // ========== Query Methods (조회) ==========

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByIdWithRoles(Long id) {
        return userRepository.findUserWithRolesById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthUser> findUserForAuthById(Long id) {
        return userRepository.findUserWithRolesById(id)
                .map(AuthUser::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthUser> findUserForAuthByEmail(String email) {
        return userRepository.findUserWithRolesByEmail(email)
                .map(AuthUser::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findUserById(Long id) {
        return findById(id).map(UserResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findUserWithRolesById(Long id) {
        return userRepository.findUserWithRolesById(id).map(UserResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    // ========== Command Methods (명령) ==========

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 비밀번호 암호화
        String encodedPassword = customPasswordEncoder.encode(request.password());

        User user = User.of(
                request.name(),
                request.nickname(),
                request.email(),
                encodedPassword,
                request.phoneNumber()
        );

        // TODO: 임시로 회원가입 시 바로 ACTIVE 상태로 설정
        // 프로덕션에서는 이메일 인증 등 추가 검증 후 활성화해야 함
        user.activateFromPending();

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUserInfo(UpdateUserInfoRequest request) {
        User user = userRepository.findUserWithRolesById(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateUserInfo(request.nickName(), request.email());
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse addInstructorRole(Long userId) {
        return addRole(userId, RoleType.INSTRUCTOR);
    }

    @Override
    @Transactional
    public void applyForInstructor(Long userId) {
        // 이미 신청한 경우 확인
        if (instructorApplicationRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException(UserErrorCode.INSTRUCTOR_APPLICATION_ALREADY_EXISTS);
        }
        InstructorApplication application = InstructorApplication.create(userId);
        instructorApplicationRepository.save(application);
    }

    private UserResponse addRole(Long userId, RoleType roleType) {
        User user = userRepository.findUserWithRolesById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.addRole(roleType);
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse withdrawUser(WithdrawUserRequest request) {
        User user = userRepository.findUserWithRolesById(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.withdraw();
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse activateUser(ActivateUserRequest request) {
        User user = userRepository.findUserWithRolesById(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.activate();
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        String encodedPassword = customPasswordEncoder.encode(request.newPassword());
        user.changePassword(encodedPassword);
    }

    @Override
    @Transactional
    public void deleteUser(DeleteUserRequest request) {
        User user = userRepository.findUserWithRolesById(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.delete();
    }

    @Override
    @Transactional
    public void processInstructorApplication(Long userId, Long applicationId, InstructorApplicationStatus status) {
        InstructorApplication application = instructorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.INSTRUCTOR_APPLICATION_NOT_FOUND));

        // 이미 처리된 신청은 수정할 수 없음
        if (application.getStatus() != InstructorApplicationStatus.PENDING) {
            throw new BusinessException(UserErrorCode.INSTRUCTOR_APPLICATION_ALREADY_PROCESSED);
        }

        if (status == InstructorApplicationStatus.APPROVED) {
            application.approve();
            instructorApplicationRepository.save(application);
            // 역할 추가
            addRole(application.getUserId(), RoleType.INSTRUCTOR);
        } else if (status == InstructorApplicationStatus.REJECTED) {
            application.reject();
            instructorApplicationRepository.save(application);
        }
    }
}

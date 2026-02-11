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
import com.lxp.aplus.user.application.dto.response.InstructorApplicationCreateResponse;
import com.lxp.aplus.user.application.dto.response.InstructorApplicationResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return userRepository.findUserWithRolesById(id)
                .map(user -> {
                    InstructorApplicationStatus status = instructorApplicationRepository.findByUserId(id)
                            .map(InstructorApplication::getStatus)
                            .orElse(InstructorApplicationStatus.NOT_APPLIED);
                    return UserResponse.from(user, status);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstructorApplicationResponse> getInstructorApplications(InstructorApplicationStatus status, Pageable pageable) {
        Page<InstructorApplication> applications = (status != null)
                ? instructorApplicationRepository.findAllByStatus(status, pageable)
                : instructorApplicationRepository.findAll(pageable);

        // userId 목록 추출
        List<Long> userIds = applications.getContent().stream()
                .map(InstructorApplication::getUserId)
                .distinct()
                .toList();

        // user 정보 batch 조회
        Map<Long, User> userMap = userRepository.findByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // InstructorApplicationListItem으로 변환
        List<InstructorApplicationResponse> items = applications.getContent().stream()
                .map(application -> {
                    User user = userMap.get(application.getUserId());
                    return InstructorApplicationResponse.of(application, user.getEmail(), user.getNickName());
                })
                .toList();

        return new PageImpl<>(items, pageable, applications.getTotalElements());
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
        return addRole(userId);
    }

    @Override
    @Transactional
    public InstructorApplicationCreateResponse applyForInstructor(Long userId) {
        // 이미 신청한 경우 확인
        if (instructorApplicationRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException(UserErrorCode.INSTRUCTOR_APPLICATION_ALREADY_EXISTS);
        }
        InstructorApplication application = InstructorApplication.create(userId);
        InstructorApplication savedApplication = instructorApplicationRepository.save(application);
        return InstructorApplicationCreateResponse.from(savedApplication);
    }

    private UserResponse addRole(Long userId) {
        User user = userRepository.findUserWithRolesById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.addRole(RoleType.INSTRUCTOR);
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

        if (status == InstructorApplicationStatus.APPROVED) {
            application.approve();
            instructorApplicationRepository.save(application);
            // 역할 추가
            addRole(application.getUserId());
        } else if (status == InstructorApplicationStatus.REJECTED) {
            application.reject();
            instructorApplicationRepository.save(application);
        }
    }
}

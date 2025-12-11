package com.lxp.aplus.user.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.security.CustomPasswordEncoder;
import com.lxp.aplus.user.application.dto.ActivateUserRequest;
import com.lxp.aplus.user.application.dto.ChangePasswordRequest;
import com.lxp.aplus.user.application.dto.CreateUserRequest;
import com.lxp.aplus.user.application.dto.DeleteUserRequest;
import com.lxp.aplus.user.application.dto.UpdateUserInfoRequest;
import com.lxp.aplus.user.application.dto.UserResponse;
import com.lxp.aplus.user.application.dto.WithdrawUserRequest;
import com.lxp.aplus.user.domain.RoleType;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import com.lxp.aplus.common.error.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User 명령(쓰기) UseCase
 * 
 * 데이터 변경 작업을 담당.
 * - @Transactional로 트랜잭션 관리
 * - 조회는 UserQueryUseCase를 통해 수행
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandUseCase {

    private final UserRepository userRepository;
    private final UserQueryUseCase userQueryUseCase;
    private final CustomPasswordEncoder customPasswordEncoder;

    public UserResponse createUser(CreateUserRequest request) {
        // 비밀번호 암호화
        String encodedPassword = customPasswordEncoder.encode(request.password());

        User user = User.of(
                request.name(),
                request.nickName(),
                request.email(),
                encodedPassword,
                request.phoneNumber()
        );

        // TODO: 임시로 회원가입 시 바로 ACTIVE 상태로 설정
        // 프로덕션에서는 이메일 인증 등 추가 검증 후 활성화해야 함
        user.activateFromPending();

        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse updateUserInfo(UpdateUserInfoRequest request) {
        User user = userQueryUseCase.findByIdWithRoles(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        
        user.updateUserInfo(request.nickName(), request.email());
        return UserResponse.from(user);
    }

    public UserResponse addInstructorRole(Long userId) {
        return addRole(userId, RoleType.INSTRUCTOR);
    }

    private UserResponse addRole(Long userId, RoleType roleType) {
        User user = userQueryUseCase.findByIdWithRoles(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.addRole(roleType);
        return UserResponse.from(user);
    }

    public UserResponse withdrawUser(WithdrawUserRequest request) {
        User user = userQueryUseCase.findByIdWithRoles(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.withdraw();
        return UserResponse.from(user);
    }

   public UserResponse activateUser(ActivateUserRequest request) {
        User user = userQueryUseCase.findByIdWithRoles(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.activate();
        return UserResponse.from(user);
    }

    /**
     * 비밀번호 변경
     * 
     * @param request 비밀번호 변경 요청 (userId, newPassword)
     */
    public void changePassword(ChangePasswordRequest request) {
        User user = userQueryUseCase.findById(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 비밀번호 암호화
        String encodedPassword = customPasswordEncoder.encode(request.newPassword());
        user.changePassword(encodedPassword);
    }

    public void deleteUser(DeleteUserRequest request) {
        // deleteCourse() 메서드에서 roles에 접근하므로 roles를 함께 로드해야 함
        User user = userQueryUseCase.findByIdWithRoles(request.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.delete();
    }
}


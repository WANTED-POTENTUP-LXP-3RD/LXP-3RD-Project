package com.lxp.aplus.user.application.usecase;

import com.lxp.aplus.user.application.dto.UserResponse;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User 조회 UseCase
 * 
 * 읽기 전용 작업을 담당.
 * - @Transactional(readOnly = true)로 성능 최적화
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryUseCase {

    private final UserRepository userRepository;

    /**
     * ID로 User 조회 (내부용)
     * 
     * 같은 패키지(application.usecase) 내에서만 사용 가능.
     * 외부(Controller 등)에서는 DTO를 반환하는 메서드를 사용해야 함.
     * 
     * @param id User ID
     * @return Optional<User>
     */
    Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * ID로 User와 Role을 함께 조회 (내부용)
     * 
     * 같은 패키지(application.usecase) 내에서만 사용 가능.
     * roles를 확인해야 하는 경우 사용 (예: addRole)
     * 
     * @param id User ID
     * @return Optional<User>
     */
    Optional<User> findByIdWithRoles(Long id) {
        return userRepository.findUserWithRolesById(id);
    }

    /**
     * ID로 User 조회 (외부용 - DTO 반환)
     * 
     * @param id User ID
     * @return Optional<UserResponse>
     */
    public Optional<UserResponse> findUserById(Long id) {
        return findById(id)
                .map(UserResponse::from);
    }

    /**
     * ID로 User와 Role을 함께 조회 (외부용 - DTO 반환)
     * 
     * Fetch Join을 사용하여 N+1 문제를 방지합니다.
     * 
     * @param id User ID
     * @return Optional<UserResponse>
     */
    public Optional<UserResponse> findUserWithRolesById(Long id) {
        return userRepository.findUserWithRolesById(id)
                .map(UserResponse::from);
    }

    /**
     * User 존재 여부 확인
     * 
     * @param id User ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}


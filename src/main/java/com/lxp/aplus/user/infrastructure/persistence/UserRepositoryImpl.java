package com.lxp.aplus.user.infrastructure.persistence;

import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository 인터페이스의 구현체
 * 
 * 아키텍처 구조:
 * - Domain Layer: UserRepository (인터페이스, 의존성 없음)
 * - Infrastructure Layer: UserRepositoryImpl (현재 클래스)
 *   - UserRepository 인터페이스를 구현
 *   - UserJpaRepository를 사용하여 JPA 기본 메서드 활용
 *   - 복잡한 비즈니스 로직이 필요한 경우 여기서 추가 구현
 * 
 * 역할:
 * - 도메인 레이어의 UserRepository 인터페이스를 구현
 * - UserJpaRepository의 JPA 기본 메서드를 활용하여 도메인 인터페이스 구현
 * - 추가 커스텀 메서드가 필요한 경우 여기서 구현
 * 
 * 사용 예시:
 * Application Layer에서 인터페이스 타입으로 주입받으면,
 * Spring이 자동으로 UserRepositoryImpl을 주입합니다.
 * 실제 호출: UserRepositoryImpl.save() → UserJpaRepository.save()
 * 
 * 커스텀 메서드 추가 방법:
 * 1. UserRepository 인터페이스에 메서드 선언
 * 2. UserJpaRepository에 쿼리 메서드 추가 (필요시)
 * 3. UserRepositoryImpl에서 구현 (UserJpaRepository 활용)
 * 
 * @see UserRepository
 * @see UserJpaRepository
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<User> findByIdIn(List<Long> ids) {
        return jpaRepository.findByIdIn(ids);
    }

    @Override
    public Optional<User> findUserWithRolesById(Long id) {
        // @EntityGraph만 사용: List로 받아서 첫 번째 요소만 반환 (중복 제거)
        // 쿼리를 직접 작성하지 않고 Spring Data JPA의 메서드 네이밍 컨벤션만 사용
        java.util.List<User> users = jpaRepository.findAllById(id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        // 첫 번째 User만 반환 (같은 User이므로 중복 제거)
        User user = users.get(0);
        // @EntityGraph로 조회했지만 Lazy Loading 프록시가 초기화되지 않을 수 있으므로
        // roles 컬렉션에 접근하여 프록시 초기화 강제
        if (user.getRoles() != null) {
            user.getRoles().size(); // 컬렉션 초기화
        }
        return Optional.of(user);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserWithRolesByEmail(String email) {
        // @EntityGraph만 사용: List로 받아서 첫 번째 요소만 반환 (중복 제거)
        // 쿼리를 직접 작성하지 않고 Spring Data JPA의 메서드 네이밍 컨벤션만 사용
        java.util.List<User> users = jpaRepository.findAllByEmail(email);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        // 첫 번째 User만 반환 (같은 User이므로 중복 제거)
        User user = users.get(0);
        // @EntityGraph로 조회했지만 Lazy Loading 프록시가 초기화되지 않을 수 있으므로
        // roles 컬렉션에 접근하여 프록시 초기화 강제
        if (user.getRoles() != null) {
            user.getRoles().size(); // 컬렉션 초기화
        }
        return Optional.of(user);
    }

}


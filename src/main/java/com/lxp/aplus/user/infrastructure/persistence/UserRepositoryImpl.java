package com.lxp.aplus.user.infrastructure.persistence;

import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

}


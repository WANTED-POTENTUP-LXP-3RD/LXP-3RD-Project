package com.lxp.aplus.user.infrastructure.persistence;

import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository
 * 
 * 헥사고날 아키텍처 관점에서 인프라스트럭처 레이어에 위치한 JPA 전용 Repository.
 * 
 * 도메인 레이어의 UserRepository와 분리한 이유:
 * - 도메인 레이어가 JPA에 의존하지 않도록 격리
 * - 기술 스택 변경 시 도메인 레이어 영향 없음 (JPA → MongoDB 등)
 * - 도메인 로직과 인프라스트럭처 관심사 분리
 * 
 * 역할:
 * - JpaRepository를 상속받아 JPA 기본 메서드 제공 (save, findById, findAll, delete 등)
 * - Spring Data JPA가 자동으로 구현체를 생성
 * - UserRepositoryImpl에서 이 인터페이스를 사용하여 도메인 인터페이스 구현
 * 
 * 커스텀 쿼리 메서드 추가 시:
 * 1. 도메인 레이어의 UserRepository 인터페이스에 메서드 선언
 * 2. 필요시 이 인터페이스에 Spring Data JPA 쿼리 메서드 추가
 *    예: findByEmail(String email), findByStatus(UserStatus status) 등
 * 3. UserRepositoryImpl에서 UserRepository 메서드를 구현 (이 인터페이스 활용)
 * 
 * @see JpaRepository
 * @see UserRepository
 * @see UserRepositoryImpl
 */
public interface UserJpaRepository extends JpaRepository<User, Long> {
}


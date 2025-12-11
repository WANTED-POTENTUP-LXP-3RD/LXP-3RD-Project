package com.lxp.aplus.user.infrastructure.persistence;

import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.domain.UserRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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
 * - JpaRepository를 상속받아 JPA 기본 메서드 제공 (save, findById, findAll, deleteCourse 등)
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
    
    /**
     * User와 Role을 함께 조회 (Fetch Join)
     * 
     * @EntityGraph를 사용하여 Fetch Join 수행
     * - attributePaths = {"roles"}로 roles 연관관계를 즉시 로딩
     * - Spring Data JPA가 자동으로 LEFT JOIN FETCH 쿼리 생성
     * - 여러 Role이 있을 경우 중복 결과가 발생할 수 있으므로 List로 반환
     * 
     * @param id User ID
     * @return List<User> (중복 제거는 RepositoryImpl에서 처리)
     */
    @EntityGraph(attributePaths = {"roles"})
    java.util.List<User> findAllById(Long id);

    /**
     * 이메일로 User 조회
     *
     * @param email 이메일
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일로 User와 Role을 함께 조회
     *
     * @EntityGraph를 사용하면 SQL에서 JOIN을 사용해 연관 엔티티까지 한번에 조회
     * - attributePaths = {"roles"}로 roles 연관관계를 한번에 조회
     * - Spring Data JPA가 자동으로 LEFT JOIN FETCH 쿼리 생성
     * - 여러 Role이 있을 경우 중복 결과가 발생할 수 있으므로 List로 반환
     *
     * @param email 이메일
     * @return List<User> (중복 제거는 RepositoryImpl에서 처리)
     */
    @EntityGraph(attributePaths = {"roles"})
    java.util.List<User> findAllByEmail(String email);
}


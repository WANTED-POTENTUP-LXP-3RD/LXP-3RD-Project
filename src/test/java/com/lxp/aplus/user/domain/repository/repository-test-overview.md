# Repository 테스트 개요

## 목적

Repository 레이어의 실제 DB 연동 및 JPA 동작을 검증하여, Infrastructure 레이어의 정확성을 보장합니다.

## 테스트 전략

### 통합 테스트 (Integration Test)

Repository 테스트는 **실제 인메모리 DB(H2)**를 사용하여 통합 테스트를 수행합니다.

#### Mock을 사용하지 않는 이유

1. **Infrastructure 레이어의 특성**
   - Repository는 기술 스택(JPA)과의 연동을 담당
   - 실제 DB 동작을 검증해야 함
   - Mock으로는 JPA의 실제 동작을 검증할 수 없음

2. **검증해야 할 항목**
   - JPA 엔티티 매핑 (`@Entity`, `@Table`, `@Column` 등)
   - 연관관계 매핑 (`@OneToMany`, `@ManyToOne`, `@JoinColumn`)
   - Cascade 동작 (`CascadeType.ALL`)
   - Fetch 전략 (`FetchType.LAZY`, `FetchType.EAGER`)
   - JPA Auditing (`@CreatedDate`, `@LastModifiedDate`)
   - DB 제약조건 (Unique, Foreign Key 등)
   - 트랜잭션 동작

3. **UserRepositoryImpl의 역할**
   - 단순히 `UserJpaRepository`를 위임하는 역할
   - Mock을 사용하면 위임 로직만 테스트하게 되어 의미가 제한적
   - 실제 JPA 동작 검증이 더 중요

### 테스트 구조

```java
@DataJpaTest
@Import(UserRepositoryImpl.class)
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
}
```

**어노테이션 설명:**
- `@DataJpaTest`: JPA 관련 컴포넌트만 로드, 인메모리 DB 사용
- `@Import(UserRepositoryImpl.class)`: Repository 구현체 명시적 등록
- `@Autowired`: 실제 Repository 구현체 주입

**테스트 환경:**
- H2 인메모리 데이터베이스 사용
- 각 테스트 후 자동 롤백 (`@Transactional` 기본 적용)
- 실제 JPA EntityManager 사용

## 검증 항목

### 1. 기본 CRUD 동작

```java
@Test
void saveAndFindById() {
    // given
    User user = User.of(...);
    
    // when
    User savedUser = userRepository.save(user);
    Optional<User> foundUser = userRepository.findById(savedUser.getId());
    
    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getId()).isNotNull(); // ID 자동 생성 검증
}
```

**검증 내용:**
- 엔티티 저장 및 ID 자동 생성
- 조회 동작
- JPA 매핑 정확성

### 2. 연관관계 동작

```java
@Test
void save_WithRole() {
    // given
    User user = User.of(...);
    user.addRole(RoleType.INSTRUCTOR);
    
    // when
    User savedUser = userRepository.save(user);
    
    // then
    assertThat(savedUser.getRoles()).hasSize(2);
    // Cascade 동작 검증: User 저장 시 Role도 함께 저장
}
```

**검증 내용:**
- `@OneToMany` 연관관계 매핑
- `CascadeType.ALL` 동작
- `@JoinColumn` 매핑

### 3. JPA Auditing

```java
@Test
void updateUserInfo() {
    // given
    User savedUser = userRepository.save(user);
    LocalDateTime beforeUpdate = savedUser.getUpdatedAt();
    
    // when
    savedUser.updateUserInfo(...);
    User updatedUser = userRepository.save(savedUser);
    
    // then
    assertThat(updatedUser.getUpdatedAt()).isAfter(beforeUpdate);
    // @LastModifiedDate 자동 업데이트 검증
}
```

**검증 내용:**
- `@CreatedDate` 자동 설정
- `@LastModifiedDate` 자동 업데이트
- `@EntityListeners(AuditingEntityListener.class)` 동작

### 4. Soft Delete

```java
@Test
void delete() {
    // given
    User savedUser = userRepository.save(user);
    
    // when
    savedUser.delete(); // deletedAt 설정
    userRepository.save(savedUser);
    
    // then
    assertThat(foundUser.get().getDeletedAt()).isNotNull();
    // Soft Delete 동작 검증
}
```

**검증 내용:**
- Soft Delete 필드 업데이트
- 실제 데이터는 DB에 유지

### 5. 도메인 로직 검증

```java
@Test
void createUser_WithDefaultRole() {
    // when
    User user = User.of(...);
    
    // then
    assertThat(user.getRoles()).hasSize(1);
    assertThat(user.getRoles().get(0).getRoleType()).isEqualTo(RoleType.STUDENT);
    // 정적 팩토리 메서드의 도메인 로직 검증
}
```

**검증 내용:**
- 도메인 메서드 동작
- 정적 팩토리 메서드 로직
- 비즈니스 규칙 적용

## Mock vs 통합 테스트 비교

### Repository 테스트 (통합 테스트)

**사용 방식:**
- 실제 H2 인메모리 DB 사용
- 실제 JPA EntityManager 사용
- 실제 트랜잭션 동작

**장점:**
- 실제 JPA 동작 검증
- DB 제약조건 검증
- 연관관계, Cascade, Fetch 전략 검증
- JPA Auditing 검증

**단점:**
- UseCase 테스트보다 느림
- 테스트 간 간섭 가능성 (롤백으로 해결)

### UseCase 테스트 (Mock 테스트)

**사용 방식:**
- Mock 객체 사용
- 실제 DB 접근 없음

**장점:**
- 빠른 실행
- 완전한 격리

**단점:**
- 실제 JPA 동작 검증 불가
- DB 제약조건 검증 불가

## 테스트 실행

### 실행 방법

```bash
# 모든 Repository 테스트 실행
./gradlew test --tests "*RepositoryTest"

# 특정 테스트 클래스 실행
./gradlew test --tests "UserRepositoryTest"

# 특정 테스트 메서드 실행
./gradlew test --tests "UserRepositoryTest.saveAndFindById"
```

### 테스트 격리

- `@DataJpaTest`는 각 테스트 메서드마다 트랜잭션 롤백
- 테스트 간 데이터 간섭 없음
- 각 테스트는 독립적으로 실행

## 주의사항

### 1. @DataJpaTest의 동작

```java
@DataJpaTest
// - JPA 관련 컴포넌트만 로드
// - 인메모리 DB(H2) 자동 설정
// - @Transactional 기본 적용 (각 테스트 후 롤백)
// - @EntityScan, @EnableJpaRepositories 자동 설정
```

### 2. @Import 필요성

```java
@Import(UserRepositoryImpl.class)
// UserRepositoryImpl은 @Repository 어노테이션이 있지만,
// @DataJpaTest는 특정 패키지만 스캔하므로 명시적 Import 필요
```

### 3. JPA Auditing 활성화

- `@DataJpaTest`는 `@EnableJpaAuditing`을 자동으로 활성화하지 않음
- 하지만 `APlusApplication`의 `@EnableJpaAuditing`이 적용됨
- 별도 설정 불필요

### 4. 트랜잭션 범위

- 각 테스트 메서드는 자동으로 트랜잭션 내에서 실행
- 테스트 종료 시 자동 롤백
- `@Transactional(propagation = Propagation.NOT_SUPPORTED)` 사용 시 주의

### 5. 실제 DB와의 차이

- H2는 실제 프로덕션 DB와 다를 수 있음
- 중요한 DB 제약조건은 별도 통합 테스트 권장
- 프로덕션 DB와 유사한 환경에서의 테스트도 고려

## Mock을 사용하지 않는 것이 맞는가?

### 결론: **맞습니다**

**이유:**

1. **Repository의 역할**
   - Infrastructure 레이어의 구현체
   - 기술 스택(JPA)과의 연동 담당
   - 실제 동작 검증이 필수

2. **검증 목적**
   - JPA 매핑, 연관관계, Cascade 등 실제 동작 검증
   - Mock으로는 이러한 검증이 불가능

3. **테스트 피라미드**
   - UseCase: 단위 테스트 (Mock 사용) - 빠르고 많음
   - Repository: 통합 테스트 (실제 DB) - 느리지만 실제 동작 검증
   - 적절한 균형 유지

4. **UserRepositoryImpl의 단순성**
   - 단순 위임 로직만 존재
   - Mock으로 테스트해도 의미가 제한적
   - 실제 JPA 동작 검증이 더 중요

### Mock을 사용할 수 있는 경우

- Repository에 복잡한 비즈니스 로직이 있는 경우
- 외부 서비스와의 연동이 있는 경우
- 하지만 현재 구조에서는 불필요

## 참고

- **UseCase 테스트**: `usecase-test-overview.md` (Mock 사용)
- **Repository 개요**: `repository-overview.md`
- **UseCase 개요**: `usecase-overview.md`


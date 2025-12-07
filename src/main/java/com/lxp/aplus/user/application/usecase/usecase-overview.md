# User UseCase 구조 설계

## 구조

### Application Layer

- **UserQueryUseCase** (읽기)
  - 데이터 조회 작업 담당
  - `@Transactional(readOnly = true)` (읽기 전용 트랜잭션)
  - `findUserById`, `findUserByIdWithRoles`, `existsById` 등

## CQRS 패턴

### Query (읽기)
- 데이터 조회 작업
- `@Transactional(readOnly = true)` 사용
- 예: 조회, 검색

### 분리 이유
- 읽기/쓰기 책임 분리
- 성능 최적화 (readOnly 트랜잭션)
- 확장성 향상

## 트랜잭션 관리

### Query UseCase
```java
@Transactional(readOnly = true)  // 읽기 전용 트랜잭션
public class UserQueryUseCase {
    // 데이터 조회 작업
}
```

### 트랜잭션이 필요한 이유
1. **Lazy Loading 지원**: 연관관계 접근 시 프록시 초기화를 위해 DB 연결 필요
2. **일관성 있는 읽기**: 같은 트랜잭션 내에서 일관된 데이터 스냅샷 보장
3. **성능 최적화**: `readOnly = true`는 DB에 읽기 전용 힌트 제공

## 영속성 컨텍스트 (Persistence Context)

### 개념
- 엔티티를 메모리에 보관하는 저장소 (1차 캐시)
- 트랜잭션 범위 내에서만 존재
- 같은 ID의 엔티티는 하나만 관리

### 동작 방식
```
1. 조회 시
   - DB에서 조회 → 영속성 컨텍스트에 저장 + 스냅샷 생성

2. 재조회 시
   - 영속성 컨텍스트에서 가져옴 (DB 조회 안 함)

3. 커밋 시
   - 읽기 전용이므로 변경 감지 없음
```

### 스냅샷 (Snapshot)
- 엔티티의 원본 상태를 저장 (변경 감지용)
- 조회 시점의 상태를 복사하여 저장
- 읽기 전용 트랜잭션에서는 변경 감지 불필요

## 프록시 (Proxy)와 Lazy Loading

### 프록시
- 실제 데이터 대신 사용되는 가짜 객체
- 연관관계 필드에 사용 (`@OneToMany(fetch = FetchType.LAZY)`)

### Lazy Loading 동작
```java
// 1. User 조회
User user = repository.findById(1L).get();
// user.roles = PersistentBag (프록시) ← 실제 데이터 없음

// 2. roles 접근 시 프록시 초기화
List<Role> roles = user.getRoles();
// SQL: SELECT * FROM roles WHERE user_id = 1
// 프록시 → 실제 List<Role>로 변환
```

### 트랜잭션과 프록시
- **트랜잭션 내**: 프록시 초기화 가능 (DB 연결 살아있음)
- **트랜잭션 밖**: `LazyInitializationException` 발생

### EAGER vs LAZY
| 구분 | LAZY | EAGER |
|------|------|-------|
| 프록시 사용 | 사용 | 사용 안 함 |
| 조회 시점 | 접근 시 | 즉시 |
| 트랜잭션 필요 | 필요 | 불필요 (이미 로딩됨) |

## 캐싱

### 영속성 컨텍스트 (1차 캐시)
- 트랜잭션 내에서 같은 ID의 엔티티는 한 번만 조회
- 재조회 시 DB 조회 없이 영속성 컨텍스트에서 반환

```java
@Transactional(readOnly = true)
public void test() {
    User user1 = repository.findById(1L).get();
    // SQL: SELECT * FROM users WHERE id = 1
    
    User user2 = repository.findById(1L).get();
    // SQL 실행 안 함! ← 영속성 컨텍스트에서 가져옴
    // user1 == user2 (같은 객체)
}
```

### 주의사항
- 영속성 컨텍스트는 트랜잭션 범위 내에서만 유효
- 트랜잭션 종료 시 영속성 컨텍스트도 종료
- 이후 엔티티 접근 시 LazyInitializationException 가능

## UseCase 사용 예시

### Query UseCase
```java
@Autowired
private UserQueryUseCase userQueryUseCase;

// User 조회 (DTO 반환)
Optional<UserResponse> user = userQueryUseCase.findUserById(1L);

// User와 Role 함께 조회 (DTO 반환)
Optional<UserResponse> userWithRoles = userQueryUseCase.findUserByIdWithRoles(1L);

// User 존재 여부 확인
boolean exists = userQueryUseCase.existsById(1L);
```

## 주의사항

### 1. 트랜잭션 범위
- UseCase 메서드는 트랜잭션 내에서 실행됨
- 엔티티를 반환할 경우, 트랜잭션 밖에서 Lazy Loading 접근 시 주의

### 2. DTO 변환 권장
- 엔티티 직접 반환보다 DTO로 변환하여 반환 권장
- 트랜잭션 내에서 DTO로 변환하면 Lazy Loading 안전

```java
@Transactional(readOnly = true)
public Optional<UserResponse> findUserById(Long id) {
    return userRepository.findById(id)
            .map(UserResponse::from);  // 트랜잭션 내에서 변환
}
```

### 3. 조인 조회 (Fetch Join)

연관관계를 함께 조회해야 할 경우 Fetch Join을 사용하여 N+1 문제를 방지하고 성능을 최적화한다.

#### N+1 문제

```java
// ❌ N+1 문제 발생
User user = repository.findById(1L).get();
List<Role> roles = user.getRoles(); // 추가 쿼리 발생
// SQL 1: SELECT * FROM users WHERE id = 1
// SQL 2: SELECT * FROM roles WHERE user_id = 1
```

**문제점:**
- User 1개 조회 시 Role 조회를 위한 추가 쿼리 발생
- 여러 User 조회 시 쿼리 수가 급증 (1 + N)

#### Fetch Join 해결 방법

**방법 1: @EntityGraph 사용 (현재 구현)**

```java
// UserJpaRepository.java
@EntityGraph(attributePaths = {"roles"})
Optional<User> findByIdWithRoles(Long id);
```

**장점:**
- `@Query` 어노테이션 없이 간단하게 구현
- Spring Data JPA가 자동으로 LEFT JOIN FETCH 쿼리 생성
- 가독성 좋음

**생성되는 SQL:**
```sql
SELECT u.*, r.*
FROM users u
LEFT JOIN roles r ON u.id = r.user_id
WHERE u.id = ?
```

**방법 2: @Query 사용**

```java
@Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
Optional<User> findByIdWithRoles(@Param("id") Long id);
```

**장점:**
- 쿼리를 직접 제어 가능
- 복잡한 조인 조건 추가 가능

**단점:**
- 쿼리 문자열 관리 필요
- 오타 위험

#### 구현 구조

```
UserQueryUseCase
  ↓
UserRepository (도메인 인터페이스)
  ↓
UserRepositoryImpl (구현체)
  ↓
UserJpaRepository (Spring Data JPA)
  ↓
@EntityGraph(attributePaths = {"roles"})
```

**코드 흐름:**

1. **도메인 인터페이스 정의**
```java
// UserRepository.java
public interface UserRepository {
    Optional<User> findByIdWithRoles(Long id);
}
```

2. **Spring Data JPA 메서드 정의**
```java
// UserJpaRepository.java
@EntityGraph(attributePaths = {"roles"})
Optional<User> findByIdWithRoles(Long id);
```

3. **구현체에서 위임**
```java
// UserRepositoryImpl.java
public Optional<User> findByIdWithRoles(Long id) {
    return jpaRepository.findByIdWithRoles(id);
}
```

4. **UseCase에서 사용**
```java
// UserQueryUseCase.java
public Optional<UserResponse> findUserByIdWithRoles(Long id) {
    return userRepository.findByIdWithRoles(id)
            .map(UserResponse::from);
}
```

#### 사용 시점

**findById() vs findByIdWithRoles()**

```java
// ❌ roles가 필요 없는 경우
Optional<UserResponse> user = userQueryUseCase.findUserById(1L);
// SQL: SELECT * FROM users WHERE id = 1

// ✅ roles가 필요한 경우
Optional<UserResponse> userWithRoles = userQueryUseCase.findUserByIdWithRoles(1L);
// SQL: SELECT u.*, r.* FROM users u LEFT JOIN roles r ON u.id = r.user_id WHERE u.id = 1
```

**선택 기준:**
- `findUserById()`: User 정보만 필요한 경우
- `findUserByIdWithRoles()`: Role 정보도 함께 필요한 경우 (DTO 변환 시 roles 필드 사용)

#### 주의사항

1. **DISTINCT 사용**
   - Fetch Join 시 중복 데이터 발생 가능
   - `@Query` 사용 시 `DISTINCT` 명시 권장
   - `@EntityGraph`는 Spring Data JPA가 자동 처리

2. **페이징 제한**
   - Fetch Join과 페이징을 함께 사용하면 문제 발생 가능
   - 페이징이 필요한 경우 별도 전략 필요

3. **성능 고려**
   - 필요한 경우에만 Fetch Join 사용
   - 불필요한 연관관계 로딩은 성능 저하 원인

### 4. Package-private 메서드
- `findById()`, `findByIdWithRoles()`는 package-private으로 설계
- 같은 패키지 내에서만 사용 가능 (내부 UseCase 간 통신용)
- 외부(Controller 등)에서는 public 메서드(`findUserById`, `findUserByIdWithRoles`) 사용

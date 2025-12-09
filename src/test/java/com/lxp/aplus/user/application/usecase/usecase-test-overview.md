# UseCase 테스트 개요

## 목적

UseCase 레이어의 비즈니스 로직을 격리된 환경에서 테스트하여, 빠르고 안정적인 단위 테스트를 수행한다.

## 테스트 전략

### Mock 기반 격리 테스트

UseCase 테스트는 **Mock 객체**를 사용하여 의존성을 격리한다.

#### 장점

1. **빠른 실행 속도**
   - 실제 DB 접근 없이 메모리에서만 실행
   - 통합 테스트 대비 빠른 실행 속도

2. **완전한 격리**
   - 다른 테스트와 독립적으로 실행
   - 외부 의존성(DB, 네트워크 등)의 영향 없음
   - 테스트 간 상호 간섭 없음

3. **단위 테스트 집중**
   - UseCase의 비즈니스 로직만 집중 테스트
   - Repository나 Infrastructure 레이어의 버그와 분리

4. **유연한 시나리오 테스트**
   - 다양한 예외 상황을 쉽게 시뮬레이션
   - 실제로 발생하기 어려운 엣지 케이스 테스트 가능

### 테스트 구조

#### 기본 설정

```java
@ExtendWith(MockitoExtension.class)
class UserQueryUseCaseTest {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserQueryUseCase userQueryUseCase;
}
```

**어노테이션 설명:**
- `@ExtendWith(MockitoExtension.class)`: Mockito 확장 기능 활성화
- `@Mock`: Mock 객체 생성 (의존성 주입용)
- `@InjectMocks`: Mock을 주입받을 대상 객체 생성

**의존성 Mock:**
- Repository 계층: 데이터 조회 Mock

## 테스트 작성 원칙

### 1. Public API만 사용

**❌ 나쁜 예:**
```java
// package-private 메서드 직접 호출
userQueryUseCase.findById(userId); // package-private 메서드
```

**✅ 좋은 예:**
```java
// public 메서드 사용
Optional<UserResponse> response = userQueryUseCase.findUserById(userId);
```

**이유:**
- UseCase 설계 의도 유지 (캡슐화)
- 테스트만을 위해 접근 제어자 변경하지 않음
- Public API만 사용하여 안정적인 테스트

### 2. Given-When-Then 패턴

```java
@Test
@DisplayName("ID로 User를 조회할 수 있다")
void findUserById() {
    // given: 테스트 데이터 준비 및 Mock 설정
    Long userId = 1L;
    User user = User.of("홍길동", "hong", "hong@example.com", "password", "010-1234-5678");
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    
    // when: 테스트 대상 메서드 실행
    Optional<UserResponse> response = userQueryUseCase.findUserById(userId);
    
    // then: 결과 검증 및 Mock 호출 검증
    assertThat(response).isPresent();
    assertThat(response.get().name()).isEqualTo("홍길동");
    verify(userRepository, times(1)).findById(userId);
}
```

### 3. 명확한 테스트 이름

- `@DisplayName`: 한글 설명 사용 (테스트 의도 명확화)
- 메서드명: `메서드명_시나리오` 형식 (예: `findUserById_Success`, `findUserById_NotFound`)

### 4. UseCase 반환값 직접 검증

**❌ 나쁜 예:**
```java
// Mock 호출 검증에만 집중
verify(userRepository, times(1)).findById(userId);
// 반환값 검증 누락
```

**✅ 좋은 예:**
```java
// UseCase의 반환값을 직접 검증
Optional<UserResponse> response = userQueryUseCase.findUserById(userId);
assertThat(response).isPresent();
assertThat(response.get().name()).isEqualTo("홍길동");
assertThat(response.get().email()).isEqualTo("hong@example.com");
verify(userRepository, times(1)).findById(userId);
```

**이유:**
- UseCase의 실제 결과를 직접 검증
- DTO 변환 로직의 정확성을 명확하게 확인
- 테스트 의도가 더 명확함

### 5. Optional 처리 검증

```java
@Test
@DisplayName("존재하지 않는 User 조회 시 Optional.empty()를 반환한다")
void findUserById_NotFound() {
    // given
    when(userRepository.findById(999L)).thenReturn(Optional.empty());
    
    // when
    Optional<UserResponse> response = userQueryUseCase.findUserById(999L);
    
    // then
    assertThat(response).isEmpty();
}
```

### 6. DTO 변환 로직 검증

UseCase의 DTO 변환 로직을 명확하게 검증한다.

**예시:**
```java
@Test
@DisplayName("ID로 User와 Role을 함께 조회할 수 있다")
void findUserByIdWithRoles() {
    // given
    User user = User.of("홍길동", "hong", "hong@example.com", "password", "010-1234-5678");
    user.addRole(RoleType.INSTRUCTOR);
    user.addRole(RoleType.ADMIN);
    when(userRepository.findByIdWithRoles(userId)).thenReturn(Optional.of(user));
    
    // when
    Optional<UserResponse> response = userQueryUseCase.findUserByIdWithRoles(userId);
    
    // then: DTO 변환 및 Role 필터링 검증
    assertThat(response).isPresent();
    assertThat(response.get().roles()).hasSize(3);
    assertThat(response.get().roles()).containsExactlyInAnyOrder(
        RoleType.STUDENT,
        RoleType.INSTRUCTOR,
        RoleType.ADMIN
    );
}
```

### 7. Soft Delete 필터링 검증

```java
@Test
@DisplayName("삭제된 Role은 조회 결과에 포함되지 않는다")
void findUserByIdWithRoles_ExcludesDeletedRoles() {
    // given
    User user = User.of("홍길동", "hong", "hong@example.com", "password", "010-1234-5678");
    user.addRole(RoleType.INSTRUCTOR);
    user.addRole(RoleType.ADMIN);
    
    // Role 하나를 삭제된 상태로 설정
    user.getRoles().stream()
        .filter(role -> role.getRoleType() == RoleType.INSTRUCTOR)
        .findFirst()
        .ifPresent(role -> {
            Role deletedRole = Role.builder()
                .id(role.getId())
                .userId(role.getUserId())
                .roleType(role.getRoleType())
                .createdAt(role.getCreatedAt())
                .deletedAt(LocalDateTime.now())
                .build();
            int index = user.getRoles().indexOf(role);
            user.getRoles().set(index, deletedRole);
        });
    
    when(userRepository.findByIdWithRoles(userId)).thenReturn(Optional.of(user));
    
    // when
    Optional<UserResponse> response = userQueryUseCase.findUserByIdWithRoles(userId);
    
    // then: 삭제된 Role이 제외되었는지 검증
    assertThat(response).isPresent();
    assertThat(response.get().roles()).hasSize(2);
    assertThat(response.get().roles()).doesNotContain(RoleType.INSTRUCTOR);
}
```

## Mock vs 통합 테스트

### Mock 테스트 (현재 방식)

**사용 시점:**
- UseCase 비즈니스 로직 검증
- DTO 변환 로직 검증
- 빠른 피드백이 필요한 경우
- 단위 테스트

**장점:**
- 빠른 실행
- 완전한 격리
- 다양한 시나리오 테스트 용이

**단점:**
- 실제 DB 동작 검증 불가
- Mock 설정이 복잡할 수 있음

### 통합 테스트

**사용 시점:**
- Repository와 DB 연동 검증
- 실제 데이터 조회 검증
- JPA 매핑 검증
- Fetch Join 동작 검증

**장점:**
- 실제 동작 검증
- DB 제약조건 검증
- N+1 문제 검증

**단점:**
- 느린 실행
- 테스트 간 간섭 가능성

## 테스트 실행

### 실행 방법

```bash
# 모든 UseCase 테스트 실행
./gradlew test --tests "*UseCaseTest"

# 특정 테스트 클래스 실행
./gradlew test --tests "UserQueryUseCaseTest"

# 특정 테스트 메서드 실행
./gradlew test --tests "UserQueryUseCaseTest.findUserById"
```

## 주의사항

### 1. 실제 동작과의 차이

- Mock은 실제 Repository 동작을 완전히 시뮬레이션하지 못할 수 있음
- Fetch Join 동작은 통합 테스트에서 검증 필요
- 중요한 통합 테스트는 별도로 작성 권장
- Mock 테스트와 통합 테스트를 병행하여 사용

### 2. Mock 설정 복잡도

- UseCase가 복잡해질수록 Mock 설정도 복잡해짐
- Mock 설정이 너무 복잡하면 UseCase 설계를 재검토 필요
- 단순한 Mock 설정이 가능하도록 UseCase를 설계하는 것이 중요

### 3. DTO 변환 로직 검증 누락

- Mock 호출 검증에만 집중하면 DTO 변환 로직 검증이 누락될 수 있음
- UseCase의 반환값을 직접 검증하여 DTO 변환 로직을 명확하게 확인

### 4. Soft Delete 필터링 검증

- `UserResponse.from()`에서 삭제된 Role을 필터링하는 로직을 검증해야 함
- 삭제된 Role이 포함되지 않았는지 명확하게 검증

## 참고

- **Mockito 문법**: `mockito-syntax-overview.md`
- **통합 테스트**: `UserRepositoryTest` (실제 DB 사용)
- **UseCase 개요**: `usecase-overview.md`
- **Repository 개요**: `repository-test-overview.md`

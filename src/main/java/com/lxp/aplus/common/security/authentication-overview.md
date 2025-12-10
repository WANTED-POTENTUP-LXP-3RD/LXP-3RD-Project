# 인증/인가 시스템 개요

## 개요

이 문서는 애플리케이션의 인증(Authentication) 및 인가(Authorization) 시스템에 대한 전반적인 설명을 제공합니다.

## 인증/인가 아키텍처

### 인증 흐름

```
1. 클라이언트 요청 (Access Token 포함)
   ↓
2. JwtAuthenticationFilter (토큰 검증 및 SecurityContext 설정)
   ↓
3. SecurityConfig (인증/권한 체크)
   ↓
4. Controller (@Authenticated 파라미터 주입)
   ↓
5. AOP (@InstructorOnly 권한 체크)
```

## 커스텀 어노테이션

### 1. @Authenticated

**위치**: `com.lxp.aplus.common.security.Authenticated`

**용도**: 현재 로그인한 사용자 정보를 메서드 파라미터로 자동 주입

**지원 타입**:
- `Long` - 사용자 ID만 반환
- `UserInfo` - 사용자 ID와 역할 목록 반환

**동작 방식**:
- `AuthenticatedArgumentResolver`가 파라미터 처리
- `SecurityUtils.getCurrentUserId()`로 현재 사용자 ID 조회
- 인증되지 않은 경우 `UNAUTHORIZED` 예외 발생

**사용 예시**:

```java
// 사용자 ID만 필요한 경우
@GetMapping("/me")
public ResponseEntity<UserResponse> getMyInfo(@Authenticated Long userId) {
    return userQueryUseCase.findUserWithRolesById(userId)
            .map(userResponse -> ResponseEntity.ok(...))
            .orElse(ResponseEntity.notFound().build());
}

// 사용자 ID와 역할 정보가 필요한 경우
@GetMapping("/me")
public ResponseEntity<UserResponse> getMyInfo(@Authenticated UserInfo userInfo) {
    // userInfo.id() - 사용자 ID
    // userInfo.roles() - 역할 목록
    // userInfo.hasRole(RoleType.INSTRUCTOR) - 역할 확인
    // userInfo.isInstructor() - 강사 권한 확인
    // userInfo.isAdmin() - 관리자 권한 확인
    
    return userQueryUseCase.findUserWithRolesById(userInfo.id())
            .map(userResponse -> ResponseEntity.ok(...))
            .orElse(ResponseEntity.notFound().build());
}
```

**에러 처리**:
- 인증되지 않은 경우: `EG006` - "인증이 필요합니다."

---

### 2. @InstructorOnly

**위치**: `com.lxp.aplus.common.security.InstructorOnly`

**용도**: 강사 권한이 필요한 메서드를 표시 (AOP로 권한 체크)

**동작 방식**:
- `InstructorOnlyAspect`가 메서드 실행 전에 권한 체크
- `@Authenticated`로 주입된 `userId` 또는 `UserInfo`에서 사용자 ID 추출
- 사용자의 역할 목록에서 `INSTRUCTOR` 역할 확인
- 강사 권한이 없으면 `NOT_INSTRUCTOR` 예외 발생

**사용 예시**:

```java
@InstructorOnly
@PatchMapping("/me/roles/instructor")
public ResponseEntity<ResultResponse<UserResponse>> addInstructorRole(
    @Authenticated UserInfo userInfo) {
    // 강사 권한이 있는 사용자만 접근 가능
    UserResponse response = userCommandUseCase.addInstructorRole(userInfo.id());
    return ResponseEntity.ok(ResultResponse.of(UserResultCode.USER_ROLE_ADD_SUCCESS, response));
}
```

**에러 처리**:
- `@Authenticated` 파라미터가 없는 경우: `EG006` - "인증이 필요합니다."
- 강사 권한이 없는 경우: `EU010` - "강사 권한이 필요합니다."

---

## 주요 컴포넌트

### 1. JwtAuthenticationFilter

**역할**: JWT 토큰 검증 및 SecurityContext 설정

**주요 기능**:
- Access Token 검증
- 만료된 Access Token 자동 갱신 (Refresh Token이 있는 경우)
- Access Token 블랙리스트 확인
- SecurityContext에 인증 정보 설정

**자동 갱신 로직**:
- Access Token이 만료되었지만 Refresh Token이 유효한 경우
- 새로운 Access Token을 생성하여 응답 헤더(`X-New-Access-Token`)에 추가
- SecurityContext에 인증 정보 설정하여 요청 계속 처리

---

### 2. AuthenticatedArgumentResolver

**역할**: `@Authenticated` 어노테이션 처리

**지원 타입**:
- `Long` - 사용자 ID만 반환 (DB 조회 없음)
- `UserInfo` - 사용자 ID와 역할 목록 반환 (DB 조회 1회)

**최적화**:
- `Long` 타입: `SecurityUtils.getCurrentUserId()`만 사용 (DB 조회 없음)
- `UserInfo` 타입: `UserQueryUseCase.findUserForAuthById()` 사용 (최신 정보 조회)

---

### 3. InstructorOnlyAspect

**역할**: `@InstructorOnly` 어노테이션 처리 (AOP)

**동작 방식**:
1. 메서드 실행 전(`@Before`) 권한 체크
2. 메서드 파라미터에서 `Long userId` 또는 `UserInfo` 추출
3. 사용자 정보 조회 및 강사 권한 확인
4. 권한이 없으면 예외 발생

---

### 4. SecurityExceptionHandler

**역할**: Spring Security 인증/권한 예외 처리

**처리하는 예외**:
- `AuthenticationException` (401 Unauthorized)
  - 토큰 없음: `EG006` - "인증이 필요합니다."
  - 만료된 토큰 (Refresh Token 없음): `EG008` - "Access Token이 만료되었습니다. 다시 로그인해주세요."
- `AccessDeniedException` (403 Forbidden)
  - `EG007` - "접근 권한이 없습니다."

---

### 5. JwtTokenProvider

**역할**: JWT 토큰 생성 및 검증

**주요 기능**:
- Access Token 생성 (짧은 만료 시간)
- Refresh Token 생성 (긴 만료 시간)
- 토큰 검증 및 만료 확인
- 토큰에서 사용자 ID 추출

---

### 6. RefreshTokenStorage

**역할**: Refresh Token 저장소 (메모리 기반)

**현재 구현**:
- `ConcurrentHashMap` 사용 (스레드 안전)
- 토큰 → 사용자 정보 매핑

**TODO**:
- 프로덕션 환경에서는 Redis로 변경 필요

---

### 7. AccessTokenBlacklist

**역할**: 로그아웃된 Access Token 관리

**주요 기능**:
- 로그아웃 시 Access Token 블랙리스트 추가
- 토큰 만료 시간까지 유지
- 만료된 토큰 자동 제거

---

## 인증 에러 코드

| 에러 코드 | HTTP 상태 | 메시지 | 발생 조건 |
|----------|----------|--------|----------|
| `EG006` | 401 | "인증이 필요합니다." | `@Authenticated`에서 인증 정보 없음, 토큰 없음 |
| `EG008` | 401 | "Access Token이 만료되었습니다. 다시 로그인해주세요." | 만료된 Access Token + Refresh Token 없음 |
| `EU007` | 401 | "비밀번호가 올바르지 않습니다." | 로그인 시 비밀번호 불일치 |
| `EU009` | 401 | "유효하지 않은 Refresh Token입니다." | Refresh Token 검증 실패 |
| `EU010` | 403 | "강사 권한이 필요합니다." | `@InstructorOnly`에서 강사 권한 없음 |
| `EG007` | 403 | "접근 권한이 없습니다." | Spring Security 권한 부족 |

---

## 사용자 정보 타입

### UserInfo

**위치**: `com.lxp.aplus.common.security.UserInfo`

**용도**: 현재 로그인한 사용자의 기본 정보 (ID, 역할 목록)

**주요 메서드**:
- `id()` - 사용자 ID
- `roles()` - 역할 목록
- `hasRole(RoleType roleType)` - 특정 역할 확인
- `isInstructor()` - 강사 권한 확인
- `isAdmin()` - 관리자 권한 확인

**사용 예시**:

```java
@Authenticated UserInfo userInfo

// 사용자 ID
Long userId = userInfo.id();

// 역할 확인
if (userInfo.isInstructor()) {
    // 강사 전용 로직
}

if (userInfo.hasRole(RoleType.ADMIN)) {
    // 관리자 전용 로직
}
```

---

## 보안 설정

### SecurityConfig

**주요 설정**:
- CSRF 비활성화 (JWT 사용)
- 세션 사용 안 함 (Stateless)
- `/api/auth/**` - 인증 불필요
- 나머지 경로 - 인증 필요
- JWT 필터 추가
- 인증/권한 예외 처리 핸들러 등록

---

## 토큰 관리

### Access Token
- **용도**: API 요청 시 인증
- **만료 시간**: 짧음 (설정 가능)
- **저장 위치**: 클라이언트 (메모리/로컬 스토리지)
- **자동 갱신**: Refresh Token으로 자동 갱신 가능

### Refresh Token
- **용도**: Access Token 갱신
- **만료 시간**: 김 (설정 가능)
- **저장 위치**: 서버 (RefreshTokenStorage)
- **갱신**: Access Token 재발급 시 기존 Refresh Token 유지

### 토큰 재발급 플로우

```
1. 클라이언트: 만료된 Access Token + Refresh Token 전송
   ↓
2. JwtAuthenticationFilter: Access Token 만료 감지
   ↓
3. Refresh Token 검증 및 사용자 정보 조회 (최신 정보)
   ↓
4. 새로운 Access Token 생성
   ↓
5. 응답 헤더에 X-New-Access-Token 추가
   ↓
6. SecurityContext에 인증 정보 설정
   ↓
7. 요청 계속 처리
```

**중요**: 토큰 재발급 시 DB에서 최신 사용자 정보를 조회하므로, Role 변경 등이 즉시 반영됩니다.

---

## 권장 사용 패턴

### 1. 사용자 ID만 필요한 경우

```java
@GetMapping("/me")
public ResponseEntity<UserResponse> getMyInfo(@Authenticated Long userId) {
    // DB 조회 없이 빠른 처리
    return userQueryUseCase.findUserWithRolesById(userId)...
}
```

### 2. 역할 정보가 필요한 경우

```java
@GetMapping("/me")
public ResponseEntity<UserResponse> getMyInfo(@Authenticated UserInfo userInfo) {
    // 역할 확인 후 처리
    if (userInfo.isInstructor()) {
        // 강사 전용 로직
    }
    return userQueryUseCase.findUserWithRolesById(userInfo.id())...
}
```

### 3. 강사 권한이 필요한 API

```java
@InstructorOnly
@PostMapping("/courses")
public ResponseEntity<CourseResponse> createCourse(@Authenticated UserInfo userInfo) {
    // 강사 권한 자동 체크
    // userInfo.id()로 사용자 ID 접근
    return courseService.createCourse(userInfo.id(), ...);
}
```

---

## 성능 고려사항

### DB 조회 최적화

1. **`@Authenticated Long userId`**:
   - DB 조회 없음 (SecurityContext에서만 조회)
   - 가장 빠름

2. **`@Authenticated UserInfo userInfo`**:
   - DB 조회 1회 (`UserQueryUseCase.findUserForAuthById`)
   - `@EntityGraph`로 User + Roles 한번에 조회 (N+1 문제 방지)

3. **`@InstructorOnly`**:
   - DB 조회 1회 (`UserRepository.findUserWithRolesById`)
   - AOP에서 권한 체크용으로 조회

### 권장사항

- 사용자 ID만 필요한 경우: `@Authenticated Long userId` 사용
- 역할 정보가 필요한 경우: `@Authenticated UserInfo userInfo` 사용
- 강사 권한 체크: `@InstructorOnly` 사용 (AOP로 자동 체크)

---

## 관련 문서

- **도메인 개요**: `domain-overview.md`
- **UseCase 개요**: `usecase-overview.md`
- **Repository 개요**: `repository-overview.md`


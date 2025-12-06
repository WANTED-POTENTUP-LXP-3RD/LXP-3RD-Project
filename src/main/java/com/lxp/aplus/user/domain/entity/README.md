# User Domain Entity 구조

## 개요
User 도메인의 엔티티 구조 및 설계 의도

## 엔티티 구조

### User (Root Aggregate)
- **역할**: User 도메인의 Root Aggregate
- **관계**: Role과 1:N 단방향 관계

#### 주요 필드
- **기본 정보**: `name`, `nickName`, `email`, `phoneNumber`, `password`
- **역할 관리**: `roles` (OneToMany, 단방향)
- **상태 관리**: `status` (UserStatus enum, 기본값: PENDING)
- **시간 추적**: `createdAt`, `updatedAt`, `deletedAt` (JPA Auditing)

#### 불변 필드
- `id`: 자동 생성, 변경 불가
- `createdAt`: 생성 시 자동 설정, 변경 불가

#### 도메인 메서드
- `createUser()`: 정적 팩토리 메서드 (기본 STUDENT 역할 자동 설정)
- `addRole()`: 역할 추가
- `updateUserInfo()`: 닉네임, 이메일 업데이트
- `updateStatus()`: 사용자 상태 업데이트
- `changePassword()`: 비밀번호 변경
- `delete()`: Soft Delete 처리

### Role
- **역할**: 사용자별 역할 관리
- **관계**: User와 1:N 관계 (단방향, User → Role)

#### 주요 필드
- `id`: 자동 생성
- `roleType`: 역할 타입 (불변, updatable = false)
- `createdAt`: 생성 시 자동 설정 (불변)
- `deletedAt`: Soft Delete용

#### 제약조건
- `(user_id, role_type)` 복합 유니크 제약조건
  - 한 사용자가 동일한 역할을 중복으로 가질 수 없음

#### 불변 필드
- `roleType`: 생성 후 변경 불가
- `createdAt`: 생성 시 자동 설정, 변경 불가

## 연관관계 설계

### User ↔ Role
- **방향**: 단방향 (User → Role)
- **타입**: OneToMany
- **매핑**: `@JoinColumn(name = "user_id")`
- **Cascade**: ALL (User 저장 시 Role도 함께 저장)
- **Fetch**: LAZY

**설계 이유**:
- Role에서 User를 조회할 필요가 없음
- 단방향으로 설계하여 불필요한 양방향 참조 제거
- User의 역할 목록만 조회하면 충분

## 불변성 설계

### User
- `id`, `createdAt`: 생성 후 변경 불가

### Role
- `roleType`: 생성 후 변경 불가 (업데이트 불가)
- 역할 변경이 필요한 경우: 기존 Role 삭제 후 새 Role 생성

## Soft Delete
- `deletedAt` 필드를 통한 Soft Delete 지원
- `delete()` 메서드로 `deletedAt`에 현재 시간 설정
- 실제 데이터는 DB에 유지, 조회 시 `deletedAt IS NULL` 조건 필요

## JPA Auditing
- `@EntityListeners(AuditingEntityListener.class)` 적용
- `createdAt`, `updatedAt` 자동 관리
- `@CreatedDate`, `@LastModifiedDate` 사용

## 패키지 구조
```
com.lxp.aplus.user.domain
├── entity/
│   ├── User.java
│   └── Role.java
└── enums/
    ├── RoleType.java
    └── UserStatus.java
```


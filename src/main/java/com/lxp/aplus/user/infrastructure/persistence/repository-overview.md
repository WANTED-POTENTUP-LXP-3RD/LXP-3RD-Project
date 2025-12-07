# User Repository 구조 설계

## 구조

### Domain Layer
- **UserRepository** (인터페이스)
  - 도메인 레이어에 위치
  - 순수 Java 인터페이스 (인프라스트럭처 의존성 없음)
  - 도메인에서 필요한 메서드만 정의

### Infrastructure Layer
- **UserRepositoryImpl** (구현체)
  - UserRepository 인터페이스 구현
  - UserJpaRepository를 사용하여 JPA 기본 메서드 활용
  - 복잡한 비즈니스 로직이 필요한 경우 여기서 구현

- **UserJpaRepository** (JPA Repository)
  - JpaRepository 상속
  - Spring Data JPA가 자동으로 구현체 생성
  - JPA 기본 메서드 제공 (save, findById, findAll 등)
  - 커스텀 쿼리 메서드 추가 가능

## 분리 이유

### 헥사고날 아키텍처 원칙
- 도메인 레이어가 인프라스트럭처에 의존하지 않도록 격리
- 기술 스택 변경 시 도메인 레이어 영향 없음 (JPA → MyBatis 등)
- 도메인 로직과 인프라스트럭처 관심사 분리

### 동작 방식
- Application Layer에서 UserRepository 인터페이스 타입으로 주입
- Spring이 자동으로 UserRepositoryImpl을 찾아서 주입
- 실제 호출: UserRepositoryImpl → UserJpaRepository → 데이터베이스

### 커스텀 메서드 추가 순서
1. 도메인 레이어의 UserRepository 인터페이스에 메서드 선언
2. 필요시 UserJpaRepository에 Spring Data JPA 쿼리 메서드 추가
3. UserRepositoryImpl에서 UserRepository 메서드 구현 (UserJpaRepository 활용)

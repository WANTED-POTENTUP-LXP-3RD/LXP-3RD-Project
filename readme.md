 # LXP-3RD-Project 온보딩 가이드

  ## 개요
  - 스택: Java 17, Spring Boot 3.4.12, Spring Data JPA/Validation/Web/Security, JWT(jjwt 0.12.3), Lombok. DB 드라이버는 MySQL, 테스트용 H2.
  - 실행: `./gradlew bootRun`, 테스트: `./gradlew test`. JPA Auditing 활성화(@EnableJpaAuditing)로 생성/수정 시간 자동 기록.
  - API 수동 호출 예제는 `http/*.http` 파일들에 정리돼 있음.

  ## 프로젝트/패키지 구조
  - 루트: `src/main/java/com/lxp/aplus`
    - `common`: 에러/보안/결과/파일 등 공통 인프라
    - `user`, `course`, `category`, `enrollment`, `progress`, `order`, `payment`, `review(빈 폴더)`: 도메인별 모듈
  - 각 도메인은 공통적으로 `domain`(엔티티, 리포지토리 인터페이스), `application`(usecase, command/result/port), `presentation`(controller, request/response DTO), `infrastructure`(JPA 구현체, 외부 어댑터)로 나눠짐. Hexagonal/CQRS 스타일을 가볍게 적용.

  ## 레이어별 컨벤션
  - Controller: `@RestController`, `@RequiredArgsConstructor`. 엔드포인트는 `/api/...`, 강사용은 `/api/instructor/...`. 응답은 `ResultResponse.of(ResultCode, data)`/`PageResponse.from()`로 감싸고 `ResponseEntity.status(resultCode.getStatus())`로 상태를 맞춤. 인증 파라미터는 `@Authenticated`(필수) 또는
  `@CurrentUser`(선택) 사용.
  - Request DTO: Java `record` + Jakarta Validation 어노테이션. 비즈니스 레이어 전달을 위해 `toCommand()` 제공(예: `CourseCreateRequest.toCommand()`).
  - Command/Result DTO: `application/command`, `application/result`에 위치. `record + @Builder` 패턴을 주로 사용해 불변성 유지.
  - UseCase: `application/usecase`에 `*CommandUseCase`(쓰기, `@Transactional`)와 `*QueryUseCase`(읽기, `@Transactional(readOnly = true)`)를 분리.
  - Domain: 엔티티는 `BaseAggregateRoot`/`BaseTimeEntity` 상속, `@Builder`/정적 팩토리(`of`, `create*`) 사용, 도메인 규칙 위반 시 `BusinessException` 던짐. 컬렉션은 기본 `ArrayList`로 초기화(`@Builder.Default`).
  - Persistence: `domain`의 리포지토리 인터페이스 + `infrastructure/persistence/*RepositoryImpl` 구현 + `*JpaRepository`(Spring Data JPA). 커스텀 조회 시 `@Query`/`@EntityGraph` 활용.
  - Port/Adapter: 다른 바운더리 호출은 `application/port/out` 인터페이스 정의 후 `infrastructure/adapter/*Adapter`에서 구현(예: `Enrollment`→`ProgressFinderAdapter`).
  - 파일 처리: 공통 인터페이스 `common/file/FileUploader`와 로컬 구현 `LocalFileUploader`를 사용.

  ## 에러/응답 규칙
  - 에러 코드: `common/error/code/*ErrorCode` Enums는 `ErrorCode` 인터페이스 구현(`HttpStatus`, `code`, `message`). 비즈니스 예외는 `BusinessException`으로 래핑.
  - 글로벌 예외 처리: `common/error/GlobalExceptionHandler`에서 `@Valid` 실패, JSON 파싱 오류, 미지정 예외 등을 `ErrorResponse`로 변환.
  - 성공 응답 코드: `common/result/code/*ResultCode` Enums. 코드 규칙은 `S{도메인}{번호}`, 에러는 `E{도메인}{번호}`.

  ## 인증/인가 흐름
  - JWT 기반 무상태. 설정은 `common/security/SecurityConfig.java`.
  - 커스텀 어노테이션: `@Authenticated`(로그인 필수, `Long`/`UserInfo` 주입), `@CurrentUser`(선택적 인증), `@InstructorOnly`(AOP로 강사 권한 확인).
  - 핵심 컴포넌트: `JwtAuthenticationFilter`(토큰 검증/갱신), `SecurityExceptionHandler`(401/403 처리), `InstructorOnlyAspect`(강사 권한 검사). 상세 흐름은 `common/security/authentication-overview.md` 참고.

  ## 주요 도메인 스냅샷
  - User (`user` 패키지)
    - 엔티티: `User`, `Role`(`RoleType`, `UserStatus`), Soft delete(`deletedAt`).
    - 기본 STUDENT 역할 자동 부여, 권한 추가/삭제는 도메인 메서드로 처리. 구조 설명은 `user/domain/domain-overview.md`.
    - UseCase 구조/트랜잭션 가이드: `user/application/usecase/usecase-overview.md`.
  - Course (`course` 패키지)
    - 엔티티: `Course`→`Section`→`Lecture`→`LectureResource`. 상태 `CourseStatus`(DRAFT/PUBLISHED/DELETED), 레벨 `CourseLevel`.
    - 커맨드/쿼리 유스케이스로 출판, 수정, 섹션/강의 CRUD 처리. 파일 업로드는 `FileUploader`를 통해 썸네일/강의 자료 URL을 생성.
  - Enrollment (`enrollment` 패키지)
    - 엔티티: `Enrollment`(`EnrollmentStatus`), 만료/취소/중복 등록 검증 도메인 메서드 보유.
    - 외부 조회 포트 `CourseFinder`, `ProgressFinder` 등과 연계.
  - Progress (`progress` 패키지)
    - 엔티티: `Progress`(강의 리소스 단위 진도/완료 여부), 유스케이스에서 진도 업데이트/조회.
  - Payment & Order (`payment`, `order` 패키지)
    - 결제 상태(`PaymentStatus`), 주문 상태(`OrderStatus`) Enum. `PaymentCommandUseCase`는 주문/수강 등록 어댑터를 호출.
  - Category (`category` 패키지)
    - 단순 트리 구조 카테고리 조회/목록.
  - Review (`review` 패키지)
    - 현재 비어 있음. 새 기능은 기존 레이어/포트 구조를 그대로 따라가는 것을 권장.

  ## 테스트 컨벤션
  - JUnit5 + AssertJ + Mockito 사용. `@DisplayName`을 한글로 명시하고 `given-when-then` 주석으로 구분.
  - 단위 테스트는 `@ExtendWith(MockitoExtension.class)` + `@Mock/@InjectMocks`. 리포지토리 테스트는 `@DataJpaTest`에 구현체 `@Import` 방식(`user/domain/repository/UserRepositoryTest.java`).
  - 테스트 데이터는 상수/로컬 변수로 선언하고, 성공/실패 케이스를 명확히 분리.

  ## 작업 시 유의사항/팁 (코스·리뷰 도메인 담당자용)
  - 새 도메인 추가 시: `domain`에 엔티티+리포지토리 인터페이스 정의 → `application`에 커맨드/쿼리 UseCase·port/result/command 작성 → `presentation`에 record 기반 Request/Response와 컨트롤러 추가 → `infrastructure`에 JPA 구현체/어댑터 작성.
  - 응답/에러 코드 Enum을 먼저 정의하고 컨트롤러에서 해당 코드로 상태/메시지를 맞춘다.
  - 인증 요구 사항은 `@Authenticated`/`@InstructorOnly`로 명시하고, 선택적 인증이면 `@CurrentUser`를 사용.
  - 벨리데이션은 Request DTO 단계에서 처리하고, 도메인 규칙 검증은 엔티티/도메인 서비스 내부에서 `BusinessException`으로 명확히 던진다.
  - 다대일/일대다 조회가 필요한 경우 `@Query`/`@EntityGraph`로 N+1을 방지하는 기존 패턴을 재사용.
  - API 수동 테스트는 `http/*.http`를 복사/수정해서 Postman 없이 실행 가능.
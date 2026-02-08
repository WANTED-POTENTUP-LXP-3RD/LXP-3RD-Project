# Progress 도메인 외부 도메인 요청서

목표: 외부 도메인이 Progress 내부 Repository/Entity를 직접 참조하지 않도록, Progress가 제공하는 내부 UseCase/DTO만 사용하게 한다.

---

## 1. 사실 기준 분석 결과

- **Progress를 사용하는 외부 도메인은 Enrollment 뿐**이다.
- course/lectureResourceV2 쪽에서 Progress를 직접 참조하는 코드는 없다.
- Progress ↔ Course 연동은 `CourseQueryToProgressUseCase`를 통해 이미 분리되어 있다.

---

## 2. Progress에서 준비한 것 (담당자 작업 완료)

### 2.1 내부 DTO (외부 매핑용)
- `ProgressInternalResult`
  - 위치: `src/main/java/com/lxp/aplus/progress/application/internal/dto/ProgressInternalResult.java`

### 2.2 내부 UseCase (외부 요청 시 사용)
- `ProgressInternalUseCase`
  - 위치: `src/main/java/com/lxp/aplus/progress/application/internal/usecase/ProgressInternalUseCase.java`
  - 메서드:
    - `findByEnrollmentIdAndResourceId(enrollmentId, lectureResourceId)`
    - `findAllByEnrollmentId(enrollmentId)`

### 2.3 요약용 내부 UseCase
- `ProgressSummaryInternalUseCase`
  - 위치: `src/main/java/com/lxp/aplus/progress/application/internal/usecase/ProgressSummaryInternalUseCase.java`
  - 메서드:
    - `getOverallProgressRate(enrollmentId, courseId)`
    - `hasProgress(enrollmentId)`
    - `removeByEnrollmentId(enrollmentId)`

---

## 3. 외부 도메인이 해야 할 일 (필요 시)

### Enrollment
- 이미 Progress 내부 UseCase를 통해 접근 중 → 변경 없음
- 사용 위치 (참고):
  - Port: `src/main/java/com/lxp/aplus/enrollment/application/port/out/ProgressQueryPort.java`
  - DTO: `src/main/java/com/lxp/aplus/enrollment/application/port/out/dto/EnrollmentProgressDto.java`
  - Adapter: `src/main/java/com/lxp/aplus/enrollment/infrastructure/adapter/module/ProgressModuleAdapter.java`

### 다른 도메인 (향후 Progress 정보 필요 시)
- Progress 엔티티/Repository 직접 접근 금지
- **ProgressInternalUseCase / ProgressSummaryInternalUseCase + DTO로 조회 후 매핑**

---

## 4. 전달 요약 한 문장

**Progress 내부 구현은 숨기고, 필요한 정보는 내부 UseCase/DTO로만 제공한다. 외부 도메인은 매핑해서 사용한다.**

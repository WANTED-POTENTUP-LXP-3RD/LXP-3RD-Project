# 🐦 Flyway 도입 가이드

## 1. Flyway 도입 계기
- 개발자마다 **DB 스키마 상태 불일치**
- DDL / 초기 데이터 SQL이 수동 실행, 복붙 실행, 실행 누락 등으로 관리됨
  - ex) 카테고리 초기값 일괄 반영

- `ddl-auto: update` 사용 시
    - 변경 이력 추적 불가
    - 환경 간(DB) 차이 발생 위험

👉 **DB 변경도 코드처럼 버전 관리할 필요**가 있어 Flyway 도입

---

## 2. Flyway가 필요한 이유

### 2.1 DB 스키마 버전 관리
- SQL 파일 = 변경 이력
- Git으로 변경 내용 추적 가능

### 2.2 환경 간 DB 상태 통일
- 로컬 / 개발 / 스테이징 / 운영
- 동일한 migration → 동일한 DB 구조

### 2.3 자동화
- 애플리케이션 실행 시 자동 적용
- 신규 팀원 DB 세팅 부담 감소

---

## 3. Flyway 개요

Flyway는 **DB 스키마 변경(DDL)과 데이터 변경(DML)** 을 *SQL 파일*로 관리하여,  
로컬 · 개발 · 스테이징 · 운영 등 **모든 환경에서 동일한 순서로 동일한 DB 상태**를 보장해주는  
**DB 마이그레이션 관리 도구**.

- **SQL 기반**으로 DB 변경을 관리.
- 변경 사항은 **버전 단위(Migration 파일 단위)** 로 누적. (`V1`, `V2`, `V3` …)
- 실행 시 Flyway는 **아직 적용되지 않은 버전만** 순서대로 적용.
- 한 번 적용된 migration은 **절대 수정 불가**
  - Flyway는 migration 파일 내용을 **checksum(해시값)** 으로 관리.
  - 이미 적용된 파일을 수정하면 `checksum mismatch` 에러로 실행 중단.
  - 변경이 필요할 경우 **항상 새 버전 migration 파일을 추가**.

### ddl-auto:update 대신 Flyway를 사용하는 이유

`ddl-auto: update` 는 실행 시점의 엔티티 상태를 기준으로  
**DB를 자동 변경**하지만, 다음과 같은 한계 존재.

- 어떤 컬럼이 **언제 / 왜** 추가·변경됐는지 알 수 없음
- 변경 이력이 남지 않아 **리뷰 및 롤백이 어려움**
- 환경별로 다른 시점에 실행되면 **DB 상태 불일치 위험**

Flyway는 DB 변경을 **명시적인 SQL + 버전 이력**으로 관리하기 때문에
- 변경 시점과 의도가 명확하고
- Git 기반 코드 리뷰가 가능하며
- 모든 환경에서 **예측 가능한 DB 변경**을 보장.

### flyway_schema_history 테이블의 역할

Flyway는 DB에 `flyway_schema_history` 테이블을 생성해 정보를 기록.

> flyway_schema_history 테이블은 **DB 변경 이력의 단일 기준(Source of Truth)** 역할  
> Flyway는 이를 기반으로 “이미 적용된 변경은 건너뛰고, 수정된 변경은 차단”.


---

## 4. 기본 동작 방식

Flyway는 실행 시점에 “현재 DB가 어디까지 적용됐는지”를 `flyway_schema_history` 기준으로 판단.
그 다음 버전부터 migration을 순서대로 적용.

1. `db/migration` 경로의 SQL 파일 탐색
2. 버전 기준 정렬
3. 미적용 migration만 실행
4. 실행 결과를 `flyway_schema_history`에 기록


---

## 5. Migration 파일 규칙

### 5.1 파일명 규칙
```text
V{버전}__{설명}.sql

EX) V1__init.sql
```

1. V : Versioned migration
2. __ (언더바 2개) 필수
3. 설명은 의미 있게 작성

### 5.2 버전 규칙

* 정수 증가 방식 사용
* V1 → V2 → V3
* 중간 버전 삽입 ❌

👉 버전 순서 = 변경 히스토리


---

## 6. 가장 중요한 원칙 🔥
> ❗ 이미 적용된 migration 파일은 절대 수정하지 않는다

* Flyway는 SQL 변경 여부를 checksum으로 검증

* 수정 시 애플리케이션 실행 실패 (checksum mismatch)

* 변경이 필요하면 반드시 새 버전 파일 추가

❌ V2__insert_category.sql 수정

✅ V3__update_category_init.sql 추가

## 7. Migration 작성 시 주의사항

### 7.1 Idempotent 하게 작성 (중복 실행에 안전)

Flyway는 migration을 한 번만 실행하지만,  
로컬 재생성 / 테스트 / 초기 데이터 작업을 고려해  
**여러 번 실행되어도 문제가 없는 SQL 작성**을 권장한다.

#### DDL – 존재 여부 체크 후 생성
```sql
CREATE TABLE IF NOT EXISTS category ( id BIGINT PRIMARY KEY, name VARCHAR(50) NOT NULL);
```

* 테이블이 이미 존재하면 아무 작업도 하지 않음
* 로컬 DB 재생성 시 안전


####  INSERT – 존재 여부 체크 후 반영 (권장)
```sql
  INSERT INTO category (id, name)
  SELECT 1, '공지'
  WHERE NOT EXISTS (
  SELECT 1 FROM category WHERE id = 1
  );
```

* 동일 ID가 이미 존재하면 INSERT 수행하지 않음
* 초기 데이터 반영에 가장 명확한 방식

#### INSERT – IGNORE 사용 (MySQL/MariaDB 한정)
```sql 
  INSERT IGNORE INTO category (id, name) VALUES (1, '공지');
```
* PK / UNIQUE 충돌 시 INSERT를 무시
* 간단하지만 다른 제약 조건 오류도 무시될 수 있어 사용 시 주의

#### (참고) 조건에 따라 조기 종료하는 방식
``` sql 
-- 이미 데이터가 존재하면 이후 SQL을 의미상 건너뜀
-- (Flyway 자체는 exit를 지원하지 않으므로,
--  조건식 기반으로 실행 여부를 제어)

INSERT INTO category (id, name)
  SELECT 1, '공지'
  FROM DUAL
  WHERE NOT EXISTS (
    SELECT 1 FROM category WHERE id = 1
  );
```
Flyway에서는 exit, return 같은 흐름 제어를 직접 지원하지 않기 때문에
조건절(WHERE / NOT EXISTS) 기반으로 안전하게 제어한다.

### 7.2 DDL과 데이터 변경 분리 (권장)

❌ 하나의 파일에 혼합
✅ 목적별 migration 분리

> V3__create_category_table.sql
V4__insert_category_init.sql

## 8. Flyway + JPA 설정
```yaml
   spring:
    jpa:
      hibernate:
        ddl-auto: validate
```
* 스키마 변경: Flyway
* JPA: 검증 역할만

## 9. 팀 협업 규칙

### ✅ 지킬 것

* 테이블 및 컬럼 추가시 DDL 추가
* 기존 migration 수정 ❌
* Migration 쿼리문은 중복 실행에 안전하도록 작성

### 🚫 금지
* 로컬에서 migration 수정 후 커밋
* 이미 실행된 migration 변경 금지





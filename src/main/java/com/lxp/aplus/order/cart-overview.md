# 🛒 Cart Bounded Context Overview

## 1. 개요
**Cart Bounded Context**는 사용자가 구매를 희망하는 강의를 임시 보관하는 영역입니다.
단순한 저장소 역할을 넘어, 외부 도메인(Course)과의 통신을 통해 실시간 가격을 반영하고 최종 결제(Order) 단계로 넘어가기 위한 데이터를 준비하는 핵심 비즈니스 로직을 담당합니다.

<br>

## 2. 핵심 비즈니스 규칙 (Business Rules)
- **1인 1바구니 보장**: 모든 사용자는 유일한 장바구니를 가집니다. (UserId - Cart : 1:1)
- **중복 담기 방지**: 동일한 강좌(CourseId)를 중복해서 담을 수 없으며, 시도 시 도메인 예외를 발생시킵니다.
- **실시간 가격 동기화**: 장바구니에 담긴 금액은 고정값이 아니며, 조회/추가/삭제 시점에 `CourseQueryPort`를 통해 최신 가격을 가져와 재계산합니다.
- **Aggregate 관리**: `CartItem`은 독립적으로 존재할 수 없으며, 반드시 Aggregate Root인 `Cart`를 통해서만 생명주기가 관리됩니다.

## 3. 컨텍스트 맵 (Context Map)
장바구니는 타 도메인과의 결합도를 낮추기 위해 **Port & Adapter** 구조를 채택하고 있습니다.



- **Course Context (Upstream)**: 강좌 정보 및 가격 정보를 제공받습니다.
- **Order Context (Downstream)**: 장바구니 데이터를 기반으로 실제 주문 프로세스를 시작합니다.

---

## 4. 도메인 모델 (Domain Model)

### **Cart (Aggregate Root)**
| 필드 | 타입 | 설명 |
| :--- | :--- | :--- |
| `id` | Long | 고유 식별자 (PK) |
| `userId` | Long | 사용자 식별자 (Unique Index) |
| `cartItems` | List | 장바구니에 담긴 아이템 목록 (`orphanRemoval = true`) |

### **CartItem (Entity)**
| 필드 | 타입 | 설명 |
| :--- | :--- | :--- |
| `id` | Long | 고유 식별자 (PK) |
| `courseId` | Long | 대상 강의 식별자 (FK 성격) |

---

## 5. 주요 유스케이스 흐름 (Key Use Case Flow)

### **강좌 추가 (Add Item)**
1. `CartRepository`에서 사용자의 장바구니 조회 (미존재 시 자동 생성 후 영속화).
2. `Cart.addCartItem(courseId)` 호출을 통해 중복 검증 및 아이템 추가.
3. `CourseQueryPort`를 통해 현재 장바구니에 담긴 모든 `courseIds`의 최신 가격 Map 조회.
4. `Cart.calculateAmount(priceMap)`로 최종 합계 계산 후 응답 반환.

### **강좌 삭제 (Remove Item)**
1. 사용자의 장바구니 조회.
2. `Cart.removeCartItem(cartItemId)` 호출.
    - 리스트에서 해당 객체를 제거하면 `orphanRemoval = true` 설정에 의해 DB 데이터도 자동 삭제됨.
3. 삭제 후 남은 항목들에 대해 가격을 재조회하여 최신 총액(Amount) 계산.
4. 삭제된 ID와 갱신된 총액을 포함하여 응답 반환.

---

## 6. 기술적 의사결정 기록 (Technical Decisions)

### **JPA 변경 감지(Dirty Checking) 활용**
- **결정**: 삭제 로직에서 명시적인 `delete` 쿼리 대신 리스트 조작을 통한 변경 감지 방식을 사용합니다.
- **이유**: 객체 지향적인 관점에서 부모가 자식의 생명주기를 관리하게 함으로써 데이터 무결성을 높이기 위함입니다.

### **Objects.equals 기반의 ID 비교**
- **결정**: 아이템 제거 시 `Objects.equals(a, b)`를 사용합니다.
- **이유**: 단위 테스트 시나 영속화 전 단계에서 발생할 수 있는 `null` ID에 의한 `NullPointerException`을 방어하기 위함입니다.

### **Port 인터페이스 분리**
- **결정**: 가격 조회를 직접 DB 조인이 아닌 별도의 `Port` 인터페이스로 분리하였습니다.
- **이유**: 향후 Course 서비스가 별도의 MSA로 분리되더라도 도메인 로직의 수정 없이 Adapter(API 호출 등)만 교체하기 위함입니다.

---

## 7. 관련 엔드포인트
- `POST /api/v1/carts/items` (장바구니 항목 추가)
- `DELETE /api/v1/carts/items/{cartItemId}` (장바구니 항목 삭제)
package com.lxp.aplus.order.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.order.application.command.CartAddItemCommand;
import com.lxp.aplus.order.application.command.CartRemoveItemCommand;
import com.lxp.aplus.order.application.port.out.CourseQueryPort;
import com.lxp.aplus.order.domain.Cart;
import com.lxp.aplus.order.domain.CartItem;
import com.lxp.aplus.order.domain.CartRepository;
import com.lxp.aplus.order.presentation.response.CartAddItemResponse;
import com.lxp.aplus.order.presentation.response.CartRemoveItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartCommandUseCase 단위 테스트")
class CartCommandUseCaseUnitTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CourseQueryPort courseQueryPort;

    @InjectMocks
    private CartCommandUseCase cartCommandUseCase;

    private final Long USER_ID = 1L;

    // -------------------------------------------------------------------------
    // 1. 정상 흐름 테스트 (Happy Path)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("강좌를 장바구니에 추가하면 실시간 가격으로 총액이 계산되어야 한다")
    void addCartItem_Success() {
        // given
        Cart cart = Cart.create(USER_ID);
        Long courseId = 100L;
        int price = 50000;

        given(cartRepository.findByUserId(USER_ID)).willReturn(Optional.of(cart));
        given(cartRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // 외부 포트가 가격 정보를 반환하도록 설정
        given(courseQueryPort.getCoursePriceByIds(anyList()))
                .willReturn(Map.of(courseId, price));

        // when
        CartAddItemResponse response = cartCommandUseCase.addCartItemToCart(new CartAddItemCommand(USER_ID, courseId));

        // then: 추가된 courseId와 계산된 금액이 정확한지 확인
        assertThat(response.amount()).isEqualTo(price);
    }

    @Test
    @DisplayName("아이템을 삭제하면 남은 강좌들의 금액 합계가 응답되어야 한다")
    void removeCartItem_Success() {
        // 1. given: 장바구니 생성 및 아이템 추가
        Cart cart = Cart.create(USER_ID);
        cart.addCartItem(1L); // 삭제 대상이 될 아이템
        cart.addCartItem(2L); // 남겨둘 아이템

        // 2. ReflectionTestUtils로 가짜 ID(PK) 주입
        // DB가 없는 단위 테스트 환경에서 getId()가 값을 반환하게 만듭니다.
        CartItem item1 = cart.getCartItems().get(0);
        CartItem item2 = cart.getCartItems().get(1);

        ReflectionTestUtils.setField(item1, "id", 100L); // 삭제할 아이템의 ID를 100으로 설정
        ReflectionTestUtils.setField(item2, "id", 200L); // 남을 아이템의 ID를 200으로 설정

        Long targetItemId = 100L; // 삭제 요청할 ID

        // 3. Mock 환경 설정
        given(cartRepository.findByUserId(USER_ID)).willReturn(Optional.of(cart));

        // 가격표 셋업: 삭제 후 남은 2번 강의(2L)의 가격이 필요함
        given(courseQueryPort.getCoursePriceByIds(anyList()))
                .willReturn(Map.of(
                        1L, 10000,
                        2L, 30000
                ));

        // 4. when: 삭제 실행
        CartRemoveItemResponse response = cartCommandUseCase.removeCartItemFromCart(
                new CartRemoveItemCommand(USER_ID, targetItemId));

        // 5. then: 검증
        // - 1번 아이템이 삭제되고, 2번 아이템(2L 강의)만 남아서 금액이 30,000원이어야 함
        assertThat(response.amount()).isEqualTo(30000);
        assertThat(cart.getCartItems()).hasSize(1);
        assertThat(cart.getCourseIds()).containsExactly(2L); // 실제로 2번 강의만 남았는지 확인
    }

    // -------------------------------------------------------------------------
    // 2. 필수 예외 테스트 (Edge Case - 장애 방지용)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("이미 장바구니에 있는 강좌를 또 담으려고 하면 예외가 발생해야 한다")
    void addCartItem_Duplicate_Fail() {
        // given: 이미 100번 강좌가 담겨있음
        Cart cart = Cart.create(USER_ID);
        cart.addCartItem(100L);
        given(cartRepository.findByUserId(USER_ID)).willReturn(Optional.of(cart));

        // when & then: 중복 추가 시 도메인 규칙 위반으로 예외 발생 확인
        assertThatThrownBy(() ->
                cartCommandUseCase.addCartItemToCart(new CartAddItemCommand(USER_ID, 100L))
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("존재하지 않는 장바구니 항목을 삭제하려 하면 예외가 발생해야 한다")
    void removeCartItem_NotFound_Fail() {
        // given: 빈 장바구니
        Cart cart = Cart.create(USER_ID);
        given(cartRepository.findByUserId(USER_ID)).willReturn(Optional.of(cart));

        // when & then: 없는 ID(999L) 삭제 시도
        assertThatThrownBy(() ->
                cartCommandUseCase.removeCartItemFromCart(new CartRemoveItemCommand(USER_ID, 999L))
        ).isInstanceOf(BusinessException.class);
    }
}
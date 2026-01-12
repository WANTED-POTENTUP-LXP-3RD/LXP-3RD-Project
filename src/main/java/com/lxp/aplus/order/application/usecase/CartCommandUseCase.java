package com.lxp.aplus.order.application.usecase;

import com.lxp.aplus.order.application.command.CartAddItemCommand;
import com.lxp.aplus.order.application.port.out.CourseQueryPort;
import com.lxp.aplus.order.domain.Cart;
import com.lxp.aplus.order.domain.CartRepository;
import com.lxp.aplus.order.presentation.response.CartAddItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CartCommandUseCase {

    private final CartRepository cartRepository;
    private final CourseQueryPort courseQueryPort;

    /*
     * 장바구니에 강좌 항목을 추가한다.
     * - 추가 후 장바구니의 실시간 상태를 반영하기 위해 전체 금액을 재계산한다.
     * - Port를 통해 외부 도메인(Course)의 가격 정보를 간접적으로 참조한다.
     */
    public CartAddItemResponse addCartItemToCart(CartAddItemCommand command) {

        // 1. cart 조회 (없으면 생성)
        Cart cart = getCart(command.userId());

        // 2. cart에 새로운 cartItem 추가
        cart.addCartItem(command.courseId());

        // 3. PK 생성을 위해 명시적으로 저장
        cartRepository.save(cart);

        // 4. 장바구니에 담긴 모든 강좌의 가격 정보를 Map 형태로 조회 (Port 사용)
        Map<Long, Integer> coursePriceMap = courseQueryPort.getCoursePriceByIds(cart.getCourseIds());

        // 5. 장바구니 총액 계산
        int amount = cart.calculateAmount(coursePriceMap);

        return CartAddItemResponse.of(cart, command.courseId(), amount);
    }

    /*
     * 장바구니에서 특정 항목을 제거한다.
     */
    public void removeCartItemFromCart(Long userId, Long courseId) {
        Cart cart = getCart(userId);
        cart.removeCartItem(courseId);
    }

    /*
     * 사용자별 장바구니를 조회하거나, 없을 경우 새 장바구니를 생성하여 반환한다.
     */
    private Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(userId)));

    }
}

package com.lxp.aplus.order.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CartErrorCode;
import com.lxp.aplus.course.domain.CourseStatus;
import com.lxp.aplus.order.application.command.CartAddItemCommand;
import com.lxp.aplus.order.application.command.CartRemoveItemCommand;
import com.lxp.aplus.order.application.port.out.CourseQueryPort;
import com.lxp.aplus.order.application.port.out.CourseSalesStatus;
import com.lxp.aplus.order.domain.Cart;
import com.lxp.aplus.order.domain.CartRepository;
import com.lxp.aplus.order.presentation.response.CartAddItemResponse;
import com.lxp.aplus.order.presentation.response.CartRemoveItemResponse;
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

        // 1. 요청된 강좌가 발행된 상태인지 검사
        if (!courseQueryPort.isCoursePublished(command.courseId())) {
            throw new BusinessException(CartErrorCode.CART_CANNOT_ADD_UNPUBLISHED_COURSE);
        }

        // 2. cart 조회 (없으면 생성)
        Cart cart = getCart(command.userId());

        // 3. cart에 새로운 cartItem 추가
        cart.addCartItem(command.courseId());

        // 4. PK 생성을 위해 명시적으로 저장
        cartRepository.save(cart);

        // 5. 장바구니에 담긴 모든 강좌의 가격 조회
        int amount = calculateCartAmount(cart);

        return CartAddItemResponse.of(cart, command.courseId(), amount);
    }

    /*
     * 장바구니에서 특정 항목을 제거한다.
     */
    public CartRemoveItemResponse removeCartItemFromCart(CartRemoveItemCommand command) {
        // 1. cart 조회 (없으면 생성)
        Cart cart = getCart(command.userId());

        // 2. cart에서 항목 제거 (dirty checking)
        cart.removeCartItem(command.cartItemId());

        // 3. 장바구니에 담긴 모든 강좌의 가격 조회
        int amount = calculateCartAmount(cart);

        return CartRemoveItemResponse.of(cart, command.cartItemId(), amount);
    }

    /*
     * 사용자별 장바구니를 조회하거나, 없을 경우 새 장바구니를 생성하여 반환한다.
     */
    private Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(userId)));
    }

    /*
     * 장바구니에 담긴 강좌들의 가격 합계를 계산한다.
     * - 장바구니에 삭제된 강좌들이 포함되어 있을 수 있으므로 PUBLISHED 강좌 가격만 계산한다.
     */
    private int calculateCartAmount(Cart cart) {
        Map<Long, CourseSalesStatus> courseSalesStatusMap = courseQueryPort.getCourseSalesStatusByIds(cart.getCourseIds());

        return cart.getCartItems().stream()
                .mapToInt(cartItem -> {
                    CourseSalesStatus courseSalesStatus = courseSalesStatusMap.get(cartItem.getCourseId());

                    // PUBLISHED 강좌만 가격 반환
                    if (courseSalesStatus != null && courseSalesStatus.status() == CourseStatus.PUBLISHED) {
                        return courseSalesStatus.price();
                    }

                    // DELETED 강좌는 0원 처리
                    return 0;
                })
                .sum();
    }
}

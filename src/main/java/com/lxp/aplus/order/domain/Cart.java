package com.lxp.aplus.order.domain;

import com.lxp.aplus.common.domain.BaseAggregateRoot;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CartErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseAggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            /*
             * - orphanRemoval = true: cartItems 리스트에서 요소가 제거되면, DB에서도 해당 행을 자동으로 삭제
             *   (즉, CartItem은 오직 Cart에 의해서만 생명주기가 관리되는 종속적인 존재임을 명시)
            */
            orphanRemoval = true
    )
    private final List<CartItem> cartItems = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    public Cart(Long userId) {
        this.userId = userId;
    }

    // === 생성 메서드 ===

    public static Cart create(Long userId) {
        return Cart.builder()
                .userId(userId)
                .build();
    }

    // === 도메인 행위 ===

    public List<CartItem> getCartItems() {
        // 읽기 전용 상태로 감싸서 반환 (도메인 무결성 보호)
        // 같은 이름의 메서드면 Lombok은 getter를 생성하지 않음!
        return Collections.unmodifiableList(cartItems);
    }

    public void addCartItem(Long courseId) {
        if (contains(courseId)) {
            throw new BusinessException(CartErrorCode.CART_DUPLICATED_CART_ITEM);
        }

        cartItems.add(CartItem.create(this, courseId));
    }

    public void removeCartItem(Long cartItemId) {
        boolean removed = cartItems.removeIf(cartItem -> cartItem.getId().equals(cartItemId));

        if (!removed) {
            throw new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND);
        }
    }

    public int calculateAmount(Map<Long, Integer> coursePriceMap) {
        return cartItems.stream()
                .map(cartItem -> coursePriceMap.getOrDefault(cartItem.getCourseId(), 0))
                .reduce(0, Integer::sum);
    }

    public List<Long> getCourseIds() {
        return cartItems.stream()
                .map(CartItem::getCourseId)
                .toList();
    }

    private boolean contains(Long courseId) {
        return cartItems.stream().anyMatch(i -> i.has(courseId));
    }
}

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

    public void removeCartItem(Long courseId) {
        cartItems.removeIf(cartItem -> cartItem.has(courseId));
    }

    private boolean contains(Long courseId) {
        return cartItems.stream().anyMatch(i -> i.has(courseId));
    }
}

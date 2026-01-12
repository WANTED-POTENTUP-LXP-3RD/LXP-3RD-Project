package com.lxp.aplus.order.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        // 같은 강의를 중복으로 담지 못하도록 DB 레벨에서 보장
                        name = "uk_cart_course",
                        columnNames = {"cart_id", "course_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Builder(access = AccessLevel.PRIVATE)
    private CartItem(Cart cart, Long courseId) {
        this.cart = cart;
        this.courseId = courseId;
    }

    public static CartItem create(Cart cart, Long courseId) {
        return CartItem.builder()
                .cart(cart)
                .courseId(courseId)
                .build();
    }

    public boolean has(Long courseId) {
        return this.courseId.equals(courseId);
    }
}

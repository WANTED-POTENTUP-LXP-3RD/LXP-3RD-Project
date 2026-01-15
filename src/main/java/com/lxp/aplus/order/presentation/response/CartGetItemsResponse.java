package com.lxp.aplus.order.presentation.response;

import com.lxp.aplus.order.application.port.out.CourseSnapshot;
import com.lxp.aplus.order.domain.Cart;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record CartGetItemsResponse(
        Long cartId,
        List<Item> items,
        int totalAmount
) {
    @Builder
    public record Item(
            Long cartItemId,
            Long courseId,
            String courseTitle,
            String courseStatus,
            String instructorName,
            String thumbnailUrl,
            int price
    ) {}

    public static CartGetItemsResponse of(Cart cart, Map<Long, CourseSnapshot> snapshotMap, int totalAmount) {

        List<Item> items = cart.getCartItems().stream()
                .map(cartItem -> {
                    CourseSnapshot courseSnapshot = snapshotMap.get(cartItem.getCourseId());

                    return Item.builder()
                            .cartItemId(cartItem.getId())
                            .courseId(courseSnapshot.courseId())
                            .courseTitle(courseSnapshot.courseTitle())
                            .courseStatus(courseSnapshot.courseStatus().name())
                            .instructorName(courseSnapshot.instructorName())
                            .thumbnailUrl(courseSnapshot.thumbnailUrl())
                            .price(courseSnapshot.price())
                            .build();
                })
                .toList();

        return CartGetItemsResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }

    public static CartGetItemsResponse empty(Long cartId) {
        return new CartGetItemsResponse(cartId, List.of(), 0);
    }
}

//package com.lxp.aplus.order.application.usecase;
//
//import com.lxp.aplus.course.domain.CourseStatus;
//import com.lxp.aplus.order.application.port.out.CourseQueryPort;
//import com.lxp.aplus.order.application.port.out.CourseSalesStatus;
//import com.lxp.aplus.order.application.port.out.CourseSnapshot;
//import com.lxp.aplus.order.domain.Cart;
//import com.lxp.aplus.order.domain.CartItem;
//import com.lxp.aplus.order.presentation.response.CartGetItemsResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class CartQueryUseCase {
//
//    private final CourseQueryPort courseQueryPort;
//
//    public CartGetItemsResponse getCartItems(Cart cart) {
//
//        // 1. 장바구니 항목 조회
//        List<CartItem> cartItems = cart.getCartItems();
//
//        // 2. 응답 DTO 변환 및 반환
//        if (cartItems.isEmpty()) {
//            return CartGetItemsResponse.empty(cart.getId());
//        }
//
//        Map<Long, CourseSnapshot> courseSnapshotMap = courseQueryPort.getCourseSnapshot(cart.getCourseIds());
//        int totalAmount = calculateCartAmount(cart);
//
//        return CartGetItemsResponse.of(cart, courseSnapshotMap, totalAmount);
//    }
//
//    /*
//     * 장바구니에 담긴 강좌들의 가격 합계를 계산한다.
//     * - 장바구니에 삭제된 강좌들이 포함되어 있을 수 있으므로 PUBLISHED 강좌 가격만 계산한다.
//     */
//    // TODO: 중복 코드 제거 방법 찾아보기(현재는 트랜잭션 문제 생길까봐 CartQueryUseCase 안에 있는 메서드 복붙해놓음)
//    private int calculateCartAmount(Cart cart) {
//        Map<Long, CourseSalesStatus> courseSalesStatusMap = courseQueryPort.getCourseSalesStatusByIds(cart.getCourseIds());
//
//        return cart.getCartItems().stream()
//                .mapToInt(cartItem -> {
//                    CourseSalesStatus courseSalesStatus = courseSalesStatusMap.get(cartItem.getCourseId());
//
//                    // PUBLISHED 강좌만 가격 반환
//                    if (courseSalesStatus != null && courseSalesStatus.status() == CourseStatus.PUBLISHED) {
//                        return courseSalesStatus.price();
//                    }
//
//                    // DELETED 강좌는 0원 처리
//                    return 0;
//                })
//                .sum();
//    }
//}

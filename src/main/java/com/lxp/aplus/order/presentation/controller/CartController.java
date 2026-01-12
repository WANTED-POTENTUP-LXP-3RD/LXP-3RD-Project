package com.lxp.aplus.order.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.order.application.usecase.CartCommandUseCase;
import com.lxp.aplus.order.presentation.request.CartAddItemRequest;
import com.lxp.aplus.order.presentation.response.CartAddItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.lxp.aplus.common.result.code.CartResultCode.CART_ADD_ITEM_SUCCESS;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartCommandUseCase cartCommandUseCase;

    @PostMapping("/items")
    public ResponseEntity<ResultResponse<CartAddItemResponse>> addCartItem(
            @Authenticated Long userId,
            @RequestBody @Valid CartAddItemRequest request
    ) {

        CartAddItemResponse response = this.cartCommandUseCase.addCartItemToCart(request.toCommand(userId));

        return ResponseEntity
                .status(CART_ADD_ITEM_SUCCESS.getStatus())
                .body(ResultResponse.of(CART_ADD_ITEM_SUCCESS, response));
    }

    //@DeleteMapping("/items/{cartItemId}")
    //public ResponseEntity<ResultResponse<CartRemoveItemResponse>> addCartItem(
    //        @Authenticated Long userId,
    //        @RequestBody @Valid CartRemoveItemRequest request
    //) {
    //
    //}
}

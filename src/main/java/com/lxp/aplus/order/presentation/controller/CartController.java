package com.lxp.aplus.order.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.order.application.command.CartRemoveItemCommand;
import com.lxp.aplus.order.application.usecase.CartCommandUseCase;
import com.lxp.aplus.order.presentation.request.CartAddItemRequest;
import com.lxp.aplus.order.presentation.response.CartAddItemResponse;
import com.lxp.aplus.order.presentation.response.CartGetItemsResponse;
import com.lxp.aplus.order.presentation.response.CartRemoveItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.lxp.aplus.common.result.code.CartResultCode.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartCommandUseCase cartCommandUseCase;

    @GetMapping
    public ResponseEntity<ResultResponse<CartGetItemsResponse>> getCartAllItems(
            @Authenticated Long userId
    ) {

        CartGetItemsResponse response = cartCommandUseCase.getCartItems(userId);

        return ResponseEntity
                .status(CART_GET_ITEMS_SUCCESS.getStatus())
                .body(ResultResponse.of(CART_GET_ITEMS_SUCCESS, response));
    }

    @PostMapping("/items")
    public ResponseEntity<ResultResponse<CartAddItemResponse>> addCartItem(
            @Authenticated Long userId,
            @RequestBody @Valid CartAddItemRequest request
    ) {

        CartAddItemResponse response = cartCommandUseCase.addCartItemToCart(request.toCommand(userId));

        return ResponseEntity
                .status(CART_ADD_ITEM_SUCCESS.getStatus())
                .body(ResultResponse.of(CART_ADD_ITEM_SUCCESS, response));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ResultResponse<CartRemoveItemResponse>> removeCartItem(
            @Authenticated Long userId,
            @PathVariable Long cartItemId
    ) {
        CartRemoveItemCommand command = CartRemoveItemCommand.of(userId, cartItemId);
        CartRemoveItemResponse response = cartCommandUseCase.removeCartItemFromCart(command);

        return ResponseEntity
                .status(CART_REMOVE_ITEM_SUCCESS.getStatus())
                .body(ResultResponse.of(CART_REMOVE_ITEM_SUCCESS, response));
    }
}

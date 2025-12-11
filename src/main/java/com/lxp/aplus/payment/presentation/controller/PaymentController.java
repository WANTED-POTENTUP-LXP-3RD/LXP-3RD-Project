package com.lxp.aplus.payment.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.payment.application.usecase.PaymentCommandUseCase;
import com.lxp.aplus.payment.presentation.request.PaymentPrepareRequest;
import com.lxp.aplus.payment.presentation.response.PaymentPrepareResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.lxp.aplus.common.result.code.PaymentResultCode.PAYMENT_PREPARE_SUCCESS;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentCommandUseCase paymentCommandUseCase;

    @PostMapping("/prepare")
    public ResponseEntity<ResultResponse<PaymentPrepareResponse>> preparePayment(
            @Authenticated Long userId,
            @RequestBody @Valid PaymentPrepareRequest request
    ) {

        PaymentPrepareResponse response = paymentCommandUseCase.prepare(request.toCommand(userId));

        return ResponseEntity
                .status(PAYMENT_PREPARE_SUCCESS.getStatus())
                .body(ResultResponse.of(PAYMENT_PREPARE_SUCCESS, response));
    }
}

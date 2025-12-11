package com.lxp.aplus.user.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.UserResultCode;
import com.lxp.aplus.user.application.dto.*;
import com.lxp.aplus.user.application.usecase.AuthCommandUseCase;
import com.lxp.aplus.user.application.usecase.UserCommandUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API Controller
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandUseCase authCommandUseCase;
    private final UserCommandUseCase userCommandUseCase;

    /**
     * 회원가입 API
     * POST /api/auth/signup
     * 
     * 새로운 사용자를 생성합니다.
     */
    @PostMapping("/signup")
    public ResponseEntity<ResultResponse<UserResponse>> signup(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userCommandUseCase.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResultResponse.of(UserResultCode.USER_CREATE_SUCCESS, response));
    }

    /**
     * 로그인 API
     * POST /api/auth/login
     * 
     * 이메일과 비밀번호로 로그인하여 Access Token과 Refresh Token을 발급합니다.
     */
    @PostMapping("/login")
    public ResponseEntity<ResultResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authCommandUseCase.login(request);
        return ResponseEntity.ok(ResultResponse.of(UserResultCode.LOGIN_SUCCESS, response));
    }

    /**
     * 로그아웃 API
     * POST /api/auth/logout
     *      *
     *      * Access Token과 Refresh Token을 무효화하여 로그아웃 처리합니다.
     * 
     * 헤더에서 토큰을 받습니다:
     * - Authorization: Bearer {accessToken} (Access Token)
     * - X-Refresh-Token: {refreshToken} (Refresh Token)
     */
    @PostMapping("/logout")
    public ResponseEntity<ResultResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        
        // Access Token 추출
        String accessToken = null;
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            accessToken = authorization.substring(7);
        }
        
        authCommandUseCase.logout(accessToken, refreshToken);
        return ResponseEntity.ok(ResultResponse.from(UserResultCode.LOGOUT_SUCCESS));
    }

    /**
     * 토큰 재발급 API
     * POST /api/auth/token/refresh
     * 
     * Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<ResultResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authCommandUseCase.refreshToken(request.refreshToken());
        return ResponseEntity.ok(ResultResponse.of(UserResultCode.TOKEN_REFRESH_SUCCESS, response));
    }

}


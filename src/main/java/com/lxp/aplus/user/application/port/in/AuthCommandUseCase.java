package com.lxp.aplus.user.application.port.in;

import com.lxp.aplus.user.application.dto.request.CreateUserRequest;
import com.lxp.aplus.user.application.dto.request.LoginRequest;
import com.lxp.aplus.user.application.dto.response.LoginResponse;

public interface AuthCommandUseCase {
    /**
     * 회원가입 및 자동 로그인
     *
     * 회원가입 후 자동으로 로그인하여 토큰을 발급합니다.
     *
     * @param request 회원가입 요청
     * @return 로그인 응답 (Access Token, Refresh Token, 닉네임, 역할 목록)
     */
    LoginResponse signupAndLogin(CreateUserRequest request);

    /**
     * 로그인
     *
     * @param request 로그인 요청 (이메일, 비밀번호)
     * @return 로그인 응답 (Access Token, Refresh Token, 닉네임, 역할 목록)
     */
    LoginResponse login(LoginRequest request);

    /**
     * 로그아웃
     *
     * Access Token과 Refresh Token을 모두 무효화합니다.
     *
     * @param accessToken Access Token (선택사항)
     * @param refreshToken Refresh Token (선택사항)
     */
    void logout(String accessToken, String refreshToken);

    /**
     * 토큰 재발급
     *
     * Role 변경 등으로 사용자 정보가 업데이트된 경우, 최신 정보를 반영하기 위해
     * DB에서 사용자 정보를 다시 조회하여 새로운 토큰을 발급합니다.
     *
     * @param refreshToken Refresh Token
     * @return 새로운 토큰 (Access Token, Refresh Token, 닉네임, 역할 목록)
     */
    LoginResponse refreshToken(String refreshToken);
}

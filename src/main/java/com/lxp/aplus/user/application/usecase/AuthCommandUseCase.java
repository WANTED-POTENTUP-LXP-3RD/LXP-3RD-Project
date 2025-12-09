package com.lxp.aplus.user.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.UserErrorCode;
import com.lxp.aplus.common.security.JwtTokenProvider;
import com.lxp.aplus.common.security.CustomPasswordEncoder;
import com.lxp.aplus.common.security.RefreshTokenStorage;
import com.lxp.aplus.common.security.AccessTokenBlacklist;
import com.lxp.aplus.user.application.dto.AuthUser;
import com.lxp.aplus.user.application.dto.CreateUserRequest;
import com.lxp.aplus.user.application.dto.LoginRequest;
import com.lxp.aplus.user.application.dto.LoginResponse;
import com.lxp.aplus.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthCommandUseCase {

    private final UserQueryUseCase userQueryUseCase;
    private final UserCommandUseCase userCommandUseCase;
    private final CustomPasswordEncoder customPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStorage refreshTokenStorage;
    private final AccessTokenBlacklist accessTokenBlacklist;

    /**
     * 회원가입 및 자동 로그인
     * 
     * 회원가입 후 자동으로 로그인하여 토큰을 발급합니다.
     * 
     * @param request 회원가입 요청
     * @return 로그인 응답 (Access Token, Refresh Token, 닉네임, 역할 목록)
     */
    public LoginResponse signupAndLogin(CreateUserRequest request) {
        // 1. 회원가입
        userCommandUseCase.createUser(request);
        
        // 2. 자동 로그인 (회원가입한 사용자로 로그인)
        LoginRequest loginRequest = new LoginRequest(request.email(), request.password());
        return login(loginRequest);
    }

    /**
     * 로그인
     * 
     * @param request 로그인 요청 (이메일, 비밀번호)
     * @return 로그인 응답 (Access Token, Refresh Token, 닉네임, 역할 목록)
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 사용자 조회 (VO로 반환) - 조회 전용 UseCase 사용
        AuthUser user = userQueryUseCase.findUserForAuthByEmail(request.email())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 2. 비밀번호 검증
        String storedPassword = user.password();
        if (storedPassword == null || storedPassword.isEmpty()) {
            log.warn("비밀번호가 저장되지 않은 사용자: userId={}, email={}", user.id(), user.email());
            throw new BusinessException(UserErrorCode.INVALID_CREDENTIALS);
        }
        
        if (!customPasswordEncoder.matches(request.password(), storedPassword)) {
            log.warn("비밀번호 검증 실패: userId={}, email={}", user.id(), user.email());
            throw new BusinessException(UserErrorCode.INVALID_CREDENTIALS);
        }

        // 3. 사용자 상태 확인
        if (user.status() != UserStatus.ACTIVE) {
            throw new BusinessException(UserErrorCode.USER_NOT_ACTIVE);
        }

        // 4. 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.id());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.id());

        // 5. Refresh Token 저장
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(refreshToken);
        LocalDateTime expirationTime = LocalDateTime.ofInstant(
                expirationDate.toInstant(),
                ZoneId.systemDefault()
        );
        refreshTokenStorage.save(user, refreshToken, expirationTime);

        log.info("로그인 성공: userId={}, email={}", user.id(), user.email());

        // Access Token 만료 시간 계산 (초 단위)
        long expiresIn = jwtTokenProvider.getAccessTokenExpirationInSeconds();
        
        // UserInfo 객체 생성
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(user.nickName(), user.roles());
        
        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                expiresIn,
                userInfo
        );
    }

    /**
     * 로그아웃
     * 
     * Access Token과 Refresh Token을 모두 무효화합니다.
     * 
     * @param accessToken Access Token (선택사항)
     * @param refreshToken Refresh Token (선택사항)
     */
    public void logout(String accessToken, String refreshToken) {
        // Refresh Token 삭제
        if (refreshToken != null && refreshTokenStorage.exists(refreshToken)) {
            refreshTokenStorage.delete(refreshToken);
        }
        
        // Access Token 블랙리스트 추가
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            try {
                // 토큰에서 만료 시간 추출
                java.util.Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(accessToken);
                java.time.LocalDateTime expirationTime = java.time.LocalDateTime.ofInstant(
                        expirationDate.toInstant(),
                        java.time.ZoneId.systemDefault()
                );
                accessTokenBlacklist.add(accessToken, expirationTime);
                log.info("Access Token 블랙리스트 추가: expirationTime={}", expirationTime);
            } catch (Exception e) {
                log.warn("Access Token 블랙리스트 추가 실패: {}", e.getMessage());
            }
        }
        
        log.info("로그아웃 성공: accessToken={}, refreshToken={}", 
                accessToken != null ? "있음" : "없음", 
                refreshToken != null ? "있음" : "없음");
    }

    /**
     * 토큰 재발급
     * 
     * Role 변경 등으로 사용자 정보가 업데이트된 경우, 최신 정보를 반영하기 위해
     * DB에서 사용자 정보를 다시 조회하여 새로운 토큰을 발급합니다.
     * 
     * @param refreshToken Refresh Token
     * @return 새로운 토큰 (Access Token, Refresh Token, 닉네임, 역할 목록)
     */
    public LoginResponse refreshToken(String refreshToken) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken) || 
            !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Refresh Token 저장소에서 사용자 ID 확인
        Long userId = refreshTokenStorage.getUserId(refreshToken);
        if (userId == null) {
            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. 최신 사용자 정보 조회 (Role 변경 등 최신 정보 반영)
        // 신규 로그인과 동일하게 DB에서 최신 사용자 정보를 조회하여
        // Role 변경 등이 반영된 최신 정보로 Access Token을 발급합니다.
        AuthUser user = userQueryUseCase.findUserForAuthById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 4. Access Token만 새로 생성 (Refresh Token은 유지)
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);

        log.info("토큰 재발급 성공: userId={}, roles={}", userId, user.roles());

        // Access Token 만료 시간 계산 (초 단위)
        long expiresIn = jwtTokenProvider.getAccessTokenExpirationInSeconds();
        
        // UserInfo 객체 생성
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(user.nickName(), user.roles());
        
        // Refresh Token은 기존 것을 그대로 반환
        return new LoginResponse(
                newAccessToken,
                refreshToken,
                "Bearer",
                expiresIn,
                userInfo
        );
    }
}


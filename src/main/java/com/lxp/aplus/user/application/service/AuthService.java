package com.lxp.aplus.user.application.service;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.UserErrorCode;
import com.lxp.aplus.common.security.JwtTokenProvider;
import com.lxp.aplus.common.security.CustomPasswordEncoder;
import com.lxp.aplus.common.security.RefreshTokenStorage;
import com.lxp.aplus.common.security.AccessTokenBlacklist;
import com.lxp.aplus.user.application.dto.AuthUser;
import com.lxp.aplus.user.application.dto.request.CreateUserRequest;
import com.lxp.aplus.user.application.dto.request.LoginRequest;
import com.lxp.aplus.user.application.dto.response.LoginResponse;
import com.lxp.aplus.user.application.port.in.AuthCommandUseCase;
import com.lxp.aplus.user.domain.User;
import com.lxp.aplus.user.application.port.out.UserRepository;
import com.lxp.aplus.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 * Auth 도메인의 통합 서비스
 * AuthCommandUseCase와 AuthQueryUseCase를 모두 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthCommandUseCase {
    private final UserRepository userRepository;
    private final CustomPasswordEncoder customPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStorage refreshTokenStorage;
    private final AccessTokenBlacklist accessTokenBlacklist;

    // ========== Command Methods (명령) ==========

    @Override
    @Transactional
    public LoginResponse signupAndLogin(CreateUserRequest request) {
        // 비밀번호 암호화
        String encodedPassword = customPasswordEncoder.encode(request.password());

        // 사용자 생성
        User user = User.of(
                request.name(),
                request.nickname(),
                request.email(),
                encodedPassword,
                request.phoneNumber()
        );

        // TODO: 임시로 회원가입 시 바로 ACTIVE 상태로 설정
        // 프로덕션에서는 이메일 인증 등 추가 검증 후 활성화해야 함
        user.activateFromPending();

        // Repository에 직접 저장
        userRepository.save(user);

        // 생성된 사용자로 바로 로그인
        LoginRequest loginRequest = new LoginRequest(request.email(), request.password());
        return login(loginRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        AuthUser user = userRepository.findUserWithRolesByEmail(request.email())
                .map(AuthUser::from)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        String storedPassword = user.password();
        if (storedPassword == null || storedPassword.isEmpty()) {
            log.warn("비밀번호가 저장되지 않은 사용자: userId={}, email={}", user.id(), user.email());
            throw new BusinessException(UserErrorCode.INVALID_CREDENTIALS);
        }

        if (!customPasswordEncoder.matches(request.password(), storedPassword)) {
            log.warn("비밀번호 검증 실패: userId={}, email={}", user.id(), user.email());
            throw new BusinessException(UserErrorCode.INVALID_CREDENTIALS);
        }

        if (user.status() != UserStatus.ACTIVE) {
            throw new BusinessException(UserErrorCode.USER_NOT_ACTIVE);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.id());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.id());

        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(refreshToken);
        LocalDateTime expirationTime = LocalDateTime.ofInstant(
                expirationDate.toInstant(),
                ZoneId.systemDefault()
        );
        refreshTokenStorage.save(user, refreshToken, expirationTime);

        log.info("로그인 성공: userId={}, email={}", user.id(), user.email());

        long expiresIn = jwtTokenProvider.getAccessTokenExpirationInSeconds();

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(user.nickName(), user.roles());

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                expiresIn,
                userInfo
        );
    }

    @Override
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        if (refreshToken != null && refreshTokenStorage.exists(refreshToken)) {
            refreshTokenStorage.delete(refreshToken);
        }

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            try {
                Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(accessToken);
                LocalDateTime expirationTime = LocalDateTime.ofInstant(
                        expirationDate.toInstant(),
                        ZoneId.systemDefault()
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

    @Override
    @Transactional(readOnly = true)
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken) ||
                !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = refreshTokenStorage.getUserId(refreshToken);
        if (userId == null) {
            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        AuthUser user = userRepository.findUserWithRolesById(userId)
                .map(AuthUser::from)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);

        log.info("토큰 재발급 성공: userId={}, roles={}", userId, user.roles());

        long expiresIn = jwtTokenProvider.getAccessTokenExpirationInSeconds();

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(user.nickName(), user.roles());

        return new LoginResponse(
                newAccessToken,
                refreshToken,
                "Bearer",
                expiresIn,
                userInfo
        );
    }
}

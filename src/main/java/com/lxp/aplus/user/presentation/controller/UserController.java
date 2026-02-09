package com.lxp.aplus.user.presentation.controller;

import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.UserResultCode;
import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.InstructorOnly;
import com.lxp.aplus.common.security.UserInfo;
import com.lxp.aplus.user.application.dto.response.UserResponse;
import com.lxp.aplus.user.application.dto.request.UpdateMyInfoRequest;
import com.lxp.aplus.user.application.dto.request.UpdateUserInfoRequest;
import com.lxp.aplus.user.application.dto.request.ChangeMyPasswordRequest;
import com.lxp.aplus.user.application.dto.request.ChangePasswordRequest;
import com.lxp.aplus.user.application.dto.request.DeleteUserRequest;
import com.lxp.aplus.user.application.port.in.UserCommandUseCase;
import com.lxp.aplus.user.application.port.in.UserQueryUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandUseCase userCommandUseCase;
    private final UserQueryUseCase userQueryUseCase;

    /**
     * 회원정보 조회 API
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ResultResponse<UserResponse>> getMyInfo(@Authenticated Long userId) {
        return userQueryUseCase.findUserWithRolesById(userId)
                .map(userResponse -> ResponseEntity.ok(
                        ResultResponse.of(UserResultCode.USER_GET_SUCCESS, userResponse)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 회원정보 수정 API
     * PATCH /api/users/me
     *
     * 현재는 닉네임만 수정 가능합니다.
     */
    @PatchMapping("/me")
    public ResponseEntity<ResultResponse<UserResponse>> updateMyInfo(
            @Authenticated UserInfo userInfo,
            @Valid @RequestBody UpdateMyInfoRequest request) {
        // 기존 사용자 정보 조회 (이메일은 변경하지 않고 기존 값 유지)
        UserResponse currentUser = userQueryUseCase.findUserWithRolesById(userInfo.id())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UpdateUserInfoRequest updateRequest = new UpdateUserInfoRequest(
                userInfo.id(),
                request.nickname(),
                currentUser.email() // 기존 이메일 유지
        );
        UserResponse response = userCommandUseCase.updateUserInfo(updateRequest);
        return ResponseEntity.ok(ResultResponse.of(UserResultCode.USER_UPDATE_SUCCESS, response));
    }

    /**
     * 회원탈퇴 API
     * DELETE /api/users/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<ResultResponse<Void>> deleteMyAccount(@Authenticated Long userId) {
        DeleteUserRequest request = new DeleteUserRequest(userId);
        userCommandUseCase.deleteUser(request);
        return ResponseEntity.ok(ResultResponse.from(UserResultCode.USER_WITHDRAW_SUCCESS));
    }

    /**
     * 비밀번호 변경 API
     * PATCH /api/users/me/password
     */
    @PatchMapping("/me/password")
    public ResponseEntity<ResultResponse<Void>> changeMyPassword(
            @Authenticated Long userId,
            @Valid @RequestBody ChangeMyPasswordRequest request) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(userId, request.newPassword());
        userCommandUseCase.changePassword(changePasswordRequest);
        return ResponseEntity.ok(ResultResponse.from(UserResultCode.USER_PASSWORD_CHANGE_SUCCESS));
    }

    /**
     * 강사 권한 추가 API
     * POST /api/users/me/roles/instructor
     * 
     * 현재 로그인한 사용자에게 강사(INSTRUCTOR) 권한을 추가합니다.
     */
    @PatchMapping("/me/roles/instructor")
    public ResponseEntity<ResultResponse<UserResponse>> addInstructorRole(@Authenticated UserInfo userInfo) {
        // UserInfo에서 id와 roles 정보를 자동으로 받아옴
        // userInfo.id() - 사용자 ID
        // userInfo.roles() - 역할 목록
        // userInfo.hasRole(RoleType.INSTRUCTOR) - 역할 확인 가능
        
        UserResponse response = userCommandUseCase.addInstructorRole(userInfo.id());
        return ResponseEntity.ok(ResultResponse.of(UserResultCode.USER_ROLE_ADD_SUCCESS, response));
    }

    /**
     * 강사 권한 요청 API
     * POST /api/users/instructor/applications
     */
    @PostMapping("/instructor/applications")
    public ResponseEntity<ResultResponse<Void>> applyForInstructor(@Authenticated Long userId) {
        userCommandUseCase.applyForInstructor(userId);
        return ResponseEntity.ok(ResultResponse.from(UserResultCode.INSTRUCTOR_APPLICATION_SUCCESS));
    }
}

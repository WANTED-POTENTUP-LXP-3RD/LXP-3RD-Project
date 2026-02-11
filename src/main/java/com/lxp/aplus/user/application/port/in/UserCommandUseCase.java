package com.lxp.aplus.user.application.port.in;

import com.lxp.aplus.user.application.dto.request.ActivateUserRequest;
import com.lxp.aplus.user.application.dto.request.ChangePasswordRequest;
import com.lxp.aplus.user.application.dto.request.CreateUserRequest;
import com.lxp.aplus.user.application.dto.request.DeleteUserRequest;
import com.lxp.aplus.user.application.dto.request.UpdateUserInfoRequest;
import com.lxp.aplus.user.application.dto.request.WithdrawUserRequest;
import com.lxp.aplus.user.application.dto.response.InstructorApplicationCreateResponse;
import com.lxp.aplus.user.application.dto.response.UserResponse;
import com.lxp.aplus.user.domain.InstructorApplicationStatus;

public interface UserCommandUseCase {
    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUserInfo(UpdateUserInfoRequest request);

    UserResponse addInstructorRole(Long userId);

    InstructorApplicationCreateResponse applyForInstructor(Long userId);

    void processInstructorApplication(Long userId, Long applicationId, InstructorApplicationStatus status);

    UserResponse withdrawUser(WithdrawUserRequest request);

    UserResponse activateUser(ActivateUserRequest request);

    void changePassword(ChangePasswordRequest request);

    void deleteUser(DeleteUserRequest request);
}

package org.sopt36.ninedotserver.user.v1.controller;

import static org.sopt36.ninedotserver.user.v1.message.UserMessage.USER_INFO_RETRIEVED_SUCCESS;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.dto.security.PrincipalDto;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.sopt36.ninedotserver.user.dto.query.UserInfoQuery;
import org.sopt36.ninedotserver.user.dto.result.UserInfoResult;
import org.sopt36.ninedotserver.user.port.in.GetUserInfoUseCase;
import org.sopt36.ninedotserver.user.v1.dto.response.UserInfoResponse;
import org.sopt36.ninedotserver.user.v1.mapper.UserRequestMapper;
import org.sopt36.ninedotserver.user.v1.mapper.UserResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final GetUserInfoUseCase getUserInfoUseCase;

    @GetMapping("/users/info")
    public ResponseEntity<ApiResponse<UserInfoResponse, Void>> getUserInfo(
        @AuthenticationPrincipal PrincipalDto principal
    ) {
        Long userId = principal.userId();
        UserInfoQuery query = UserRequestMapper.toUserInfoQuery(userId);
        UserInfoResult result = getUserInfoUseCase.execute(query);
        UserInfoResponse response = UserResponseMapper.toUserInfoResponse(result);
        return ResponseEntity.ok(ApiResponse.ok(USER_INFO_RETRIEVED_SUCCESS, response));
    }

}

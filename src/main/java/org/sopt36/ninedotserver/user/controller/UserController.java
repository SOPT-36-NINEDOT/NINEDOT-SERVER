package org.sopt36.ninedotserver.user.controller;

import static org.sopt36.ninedotserver.user.controller.message.UserMessage.USER_INFO_RETRIEVED_SUCCESS;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.user.dto.response.UserInfoResponse;
import org.sopt36.ninedotserver.user.service.query.UserQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserQueryService userQueryService;

    @GetMapping("/users/info")
    public ResponseEntity<ApiResponse<UserInfoResponse, Void>> getUserInfo() {
        Long userId = 1L;
        UserInfoResponse response = userQueryService.getUserInfo(userId);
        return ResponseEntity.ok(ApiResponse.ok(USER_INFO_RETRIEVED_SUCCESS, response));
    }

}

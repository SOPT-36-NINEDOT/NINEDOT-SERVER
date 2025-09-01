package org.sopt36.ninedotserver.user.v1;

import static org.sopt36.ninedotserver.user.v1.message.UserMessage.USER_INFO_RETRIEVED_SUCCESS;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.sopt36.ninedotserver.user.dto.response.UserInfoResponse;
import org.sopt36.ninedotserver.user.usecase.UserQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserQueryService userQueryService;

    @GetMapping("/users/info")
    public ResponseEntity<ApiResponse<UserInfoResponse, Void>> getUserInfo(
        Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        UserInfoResponse response = userQueryService.getUserInfo(userId);
        return ResponseEntity.ok(ApiResponse.ok(USER_INFO_RETRIEVED_SUCCESS, response));
    }

}

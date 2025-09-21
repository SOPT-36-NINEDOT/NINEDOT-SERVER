package org.sopt36.ninedotserver.user.v1;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.sopt36.ninedotserver.user.dto.query.GetUserInfoQuery;
import org.sopt36.ninedotserver.user.dto.result.UserInfoResult;
import org.sopt36.ninedotserver.user.usecase.port.in.GetUserInfoUseCase;
import org.sopt36.ninedotserver.user.v1.dto.response.UserInfoResponse;
import org.sopt36.ninedotserver.user.v1.mapper.UserResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.sopt36.ninedotserver.user.v1.message.UserMessage.USER_INFO_RETRIEVED_SUCCESS;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final GetUserInfoUseCase getUserInfoUseCase;
    private final UserResponseMapper userResponseMapper;

    @GetMapping("/users/info")
    public ResponseEntity<ApiResponse<UserInfoResponse, Void>> getUserInfo(
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        GetUserInfoQuery query = new GetUserInfoQuery(userId);
        UserInfoResult result = getUserInfoUseCase.getUserInfo(query);
        UserInfoResponse response = userResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.ok(USER_INFO_RETRIEVED_SUCCESS, response));
    }

}

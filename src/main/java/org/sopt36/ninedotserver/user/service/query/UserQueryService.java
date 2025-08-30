package org.sopt36.ninedotserver.user.service.query;

import static org.sopt36.ninedotserver.user.exception.UserErrorCode.USER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.user.domain.User;
import org.sopt36.ninedotserver.user.dto.response.UserInfoResponse;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserQueryService {

    private final UserRepository userRepository;

    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        return UserInfoResponse.from(user);
    }

}

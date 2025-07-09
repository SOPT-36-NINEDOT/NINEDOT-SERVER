package org.sopt36.ninedotserver.mandalart.service.command;

import static org.sopt36.ninedotserver.user.exception.UserErrorCode.USER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.dto.request.MandalartCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.MandalartCreateResponse;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.user.domain.User;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MandalartCommandService {

    private static final boolean AI_GENERATABLE = true;

    private final MandalartRepository mandalartRepository;
    private final UserRepository userRepository;

    @Transactional
    public MandalartCreateResponse createMandalart(
        Long userId,
        MandalartCreateRequest mandalartCreateRequest
    ) {
        User user = getExistingUser(userId);
        Mandalart mandalart = Mandalart.create(
            user,
            mandalartCreateRequest.title(),
            AI_GENERATABLE
        );

        mandalartRepository.save(mandalart);

        return MandalartCreateResponse.from(mandalart);
    }

    private User getExistingUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}

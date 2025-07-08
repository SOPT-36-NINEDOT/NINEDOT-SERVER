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

    /**
     * Creates a new Mandalart entity for the specified user and returns a response DTO.
     *
     * Retrieves the user by ID, throws a UserException if not found, creates a Mandalart with the provided title and a fixed AI-generatable flag, saves it, and returns a response based on the saved entity.
     *
     * @param userId the ID of the user for whom the Mandalart is created
     * @param mandalartCreateRequest the request containing Mandalart creation details
     * @return a response DTO representing the created Mandalart
     * @throws UserException if the user with the given ID does not exist
     */
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

    /**
     * Retrieves an existing User by ID or throws a UserException if not found.
     *
     * @param userId the ID of the user to retrieve
     * @return the User entity corresponding to the given ID
     * @throws UserException if no user with the specified ID exists
     */
    private User getExistingUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}

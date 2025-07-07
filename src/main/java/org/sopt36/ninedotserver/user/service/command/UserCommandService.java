package org.sopt36.ninedotserver.user.service.command;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.onboarding.repository.AnswerRepository;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCommandService {

    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

}

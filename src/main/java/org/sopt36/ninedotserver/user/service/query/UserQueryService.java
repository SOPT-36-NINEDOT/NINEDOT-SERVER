package org.sopt36.ninedotserver.user.service.query;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserQueryService {

    private final UserRepository userRepository;

}

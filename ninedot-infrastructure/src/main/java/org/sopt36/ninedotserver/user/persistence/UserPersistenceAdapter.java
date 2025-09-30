package org.sopt36.ninedotserver.user.persistence;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.persistence.jpa.repository.UserRepository;
import org.sopt36.ninedotserver.user.port.out.UserCommandPort;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserQueryPort, UserCommandPort {

    private final UserRepository userRepository;


    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public <S extends User> S saveAndFlush(S user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}

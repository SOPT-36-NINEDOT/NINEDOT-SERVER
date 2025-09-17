package org.sopt36.ninedotserver.user.persistence;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.persistence.jpa.repository.userRepository;
import org.sopt36.ninedotserver.user.port.out.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final userRepository userRepository;


    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
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

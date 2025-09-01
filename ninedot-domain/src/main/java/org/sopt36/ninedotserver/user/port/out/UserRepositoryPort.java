package org.sopt36.ninedotserver.user.port.out;

import java.util.Optional;
import java.util.List;
import org.sopt36.ninedotserver.user.model.User;

public interface UserRepositoryPort {

    Optional<User> findById(Long userId);

    <S extends User> S save(S user);

    boolean existsById(Long aLong);

    List<User> findAll();
}

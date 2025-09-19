package org.sopt36.ninedotserver.user.port.out;

import java.util.Optional;
import java.util.List;
import org.sopt36.ninedotserver.user.model.User;

public interface UserQueryPort {

    Optional<User> findById(Long userId);

    boolean existsById(Long aLong);

    List<User> findAll();
}

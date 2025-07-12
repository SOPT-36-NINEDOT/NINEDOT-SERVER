package org.sopt36.ninedotserver.mandalart.repository;

import java.util.Optional;
import org.sopt36.ninedotserver.user.domain.User;

public interface MandalartRepositoryCustom {

    Optional<User> findUserById(Long mandalartId);

    Optional<String> findTitleByMandalartId(Long mandalartId);

}

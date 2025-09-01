package org.sopt36.ninedotserver.mandalart.persistence.querydsl;

import java.util.Optional;
import org.sopt36.ninedotserver.user.model.User;

public interface MandalartRepositoryCustom {

    boolean existsByUserId(Long userId);

    Optional<User> findUserById(Long mandalartId);

    Optional<String> findTitleByCoreGoalId(Long coreGoalSnapshotId);
}

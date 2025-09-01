package org.sopt36.ninedotserver.mandalart.port.out;

import java.util.List;
import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.Mandalart;
import org.sopt36.ninedotserver.user.model.User;

public interface MandalartRepositoryPort {

    boolean existsByUserId(Long userId);

    Optional<Mandalart> findById(Long mandalartId);

    Optional<User> findUserById(Long mandalartId);

    Optional<String> findTitleByCoreGoalId(Long coreGoalSnapshotId);

    <S extends Mandalart> S save(S mandalart);

    List<Long> findIdByUserId(Long userId);
}

package org.sopt36.ninedotserver.mandalart.port.out;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.CoreGoal;

public interface CoreGoalRepositoryPort {

    boolean existsByUserId(Long userId);

    <S extends CoreGoal> S save(S coreGoal);

    void delete(CoreGoal coreGoal);

    <S extends CoreGoal> Iterable<S> saveAll(Iterable<S> coreGoals);

    Optional<CoreGoal> findByMandalartIdAndPosition(Long mandalartId, int position);

    int countCoreGoalByMandalartId(Long mandalartId);

    boolean existsByMandalartIdAndPosition(
        Long mandalartId,

        @Min(value = 1, message = "position은 1 이상 8 이하의 값이어야 합니다.")
        @Max(value = 8, message = "position은 1 이상 8 이하의 값이어야 합니다.") int position
    );

    List<CoreGoal> findByMandalartId(Long mandalartId);
}

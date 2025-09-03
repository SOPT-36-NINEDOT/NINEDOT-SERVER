package org.sopt36.ninedotserver.mandalart.port.out;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Optional;
import org.sopt36.ninedotserver.mandalart.model.CoreGoal;
import org.sopt36.ninedotserver.mandalart.model.SubGoal;

public interface SubGoalRepositoryPort {

    void deleteByCoreGoal(CoreGoal coreGoal);

    Optional<SubGoal> findByCoreGoalIdAndPosition(Long id,
        @Min(value = 1, message = "position은 1 이상 8 이하의 값이어야 합니다.") @Max(value = 8, message = "position은 1 이상 8 이하의 값이어야 합니다.") int position);

    <S extends SubGoal> S save(S subGoal);

    void delete(SubGoal subGoal);

    <S extends SubGoal> Iterable<S> saveAll(Iterable<S> subGoals);

    int countByCoreGoalId(Long coreGoalId);

    boolean existsByCoreGoalIdAndPosition(Long coreGoalId, int position);
}

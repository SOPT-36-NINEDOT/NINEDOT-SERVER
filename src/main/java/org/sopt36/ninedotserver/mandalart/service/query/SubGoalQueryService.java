package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.CORE_GOAL_NOT_FOUND;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.FORBIDDEN_ACCESS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.SubGoal;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalIdResponse;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SubGoalQueryService {

    private final SubGoalRepository subGoalRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final MandalartRepository mandalartRepository;

    public List<SubGoalIdResponse> getSubGoalIds(Long userId, Long coreGoalId) {
        CoreGoal coreGoal = coreGoalRepository.findById(coreGoalId)
            .orElseThrow(() -> new SubGoalException(CORE_GOAL_NOT_FOUND));

        if (!coreGoal.getMandalart().getUser().getId().equals(userId)) {
            throw new SubGoalException(FORBIDDEN_ACCESS);
        }

        List<SubGoal> subGoals = subGoalRepository.findAllByCoreGoalId(coreGoalId);

        return subGoals.stream()
            .map(SubGoalIdResponse::from)
            .toList();
    }

    private CoreGoal validateCoreGoalOwnerOrThrow(Long userId, Long coreGoalId) {
        CoreGoal coreGoal = coreGoalRepository.findById(coreGoalId)
            .orElseThrow(() -> new SubGoalException(CORE_GOAL_NOT_FOUND));

        coreGoal.verifyUser(userId);

        return coreGoal;
    }
}

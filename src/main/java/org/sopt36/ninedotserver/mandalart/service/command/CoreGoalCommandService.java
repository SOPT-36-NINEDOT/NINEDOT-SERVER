package org.sopt36.ninedotserver.mandalart.service.command;

import static org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode.CORE_GOAL_COMPLETED;
import static org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode.CORE_GOAL_CONFLICT;
import static org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode.CORE_GOAL_NOT_FOUND;
import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.ai.service.AiRecommendationService;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.dto.request.CoreGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.CoreGoalUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalCreateResponse;
import org.sopt36.ninedotserver.mandalart.exception.CoreGoalException;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CoreGoalCommandService {

    private static final int MAX_MANDALART = 8;
    private static final boolean AI_GENERATABLE = true;

    private final CoreGoalRepository coreGoalRepository;
    private final MandalartRepository mandalartRepository;
    private final AiRecommendationService aiRecommendationService;

    @Transactional
    public CoreGoalCreateResponse createCoreGoal(
        Long userId,
        Long mandalartId,
        CoreGoalCreateRequest coreGoalCreateRequest
    ) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        validateCanCreateCoreGoal(mandalart, userId, coreGoalCreateRequest);

        CoreGoal coreGoal = CoreGoal.create(mandalart,
            coreGoalCreateRequest.title(),
            coreGoalCreateRequest.position(),
            AI_GENERATABLE
        );
        coreGoalRepository.save(coreGoal);

        return CoreGoalCreateResponse.from(coreGoal);
    }

    @Transactional
    public void updateCoreGoal(
        Long userId,
        Long coreGoalId,
        CoreGoalUpdateRequest coreGoalUpdateRequest
    ) {
        CoreGoal coreGoal = getExistingCoreGoal(coreGoalId);
        coreGoal.verifyUser(userId);

        coreGoal.updateTitle(coreGoalUpdateRequest.title());

        coreGoalRepository.save(coreGoal);
    }

    private Mandalart getExistingMandalart(Long mandalartId) {
        return mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));
    }

    private void validateCanCreateCoreGoal(Mandalart mandalart,
        Long userId,
        CoreGoalCreateRequest coreGoalCreateRequest
    ) {
        mandalart.ensureOwnedBy(userId);
        validateCoreGoalLimitNotExceeded(mandalart.getId());
        validateAlreadyExists(mandalart.getId(), coreGoalCreateRequest);
    }

    private void validateCoreGoalLimitNotExceeded(Long mandalartId) {
        if (coreGoalRepository.countCoreGoalByMandalartId(mandalartId) == MAX_MANDALART) {
            throw new CoreGoalException(CORE_GOAL_COMPLETED);
        }
    }

    private void validateAlreadyExists(Long mandalartId,
        CoreGoalCreateRequest coreGoalCreateRequest
    ) {
        if (coreGoalRepository.existsByMandalartIdAndPosition(mandalartId,
            coreGoalCreateRequest.position())) {
            throw new CoreGoalException(CORE_GOAL_CONFLICT);
        }
    }

    private CoreGoal getExistingCoreGoal(Long coreGoalId) {
        return coreGoalRepository.findById(coreGoalId)
            .orElseThrow(() -> new CoreGoalException(CORE_GOAL_NOT_FOUND));
    }
}

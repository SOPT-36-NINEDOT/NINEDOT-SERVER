package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;

import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalDetailResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalIdResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalIdsResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalsResponse;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalSnapshotRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CoreGoalQueryService {

    private final CoreGoalRepository coreGoalRepository;
    private final MandalartRepository mandalartRepository;
    private final CoreGoalSnapshotRepository coreGoalSnapshotRepository;

    public CoreGoalIdsResponse getCoreGoalIds(Long userId, Long mandalartId) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);

        List<CoreGoalIdResponse> ids = findCoreGoalIds(mandalartId);

        return CoreGoalIdsResponse.of(ids);
    }

    public CoreGoalsResponse getCoreGoals(Long userId, Long mandalartId) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);

        List<CoreGoalDetailResponse> coreGoals = findCoreGoalDetails(mandalartId);

        return CoreGoalsResponse.of(coreGoals);
    }

    private Mandalart getExistingMandalart(Long mandalartId) {
        return mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));
    }

    private List<CoreGoalIdResponse> findCoreGoalIds(Long mandalartId) {
        return findCoreGoals(mandalartId, CoreGoalIdResponse::from);
    }

    private List<CoreGoalDetailResponse> findCoreGoalDetails(Long mandalartId) {
        return findCoreGoals(mandalartId, CoreGoalDetailResponse::from);
    }

    private <T> List<T> findCoreGoals(Long mandalartId, Function<CoreGoalSnapshot, T> mapper) {
        return coreGoalSnapshotRepository
            .findByMandalartIdOrderByPosition(mandalartId)
            .stream()
            .map(mapper)
            .toList();
    }

}

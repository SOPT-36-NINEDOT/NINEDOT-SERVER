package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalBoardResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.MandalartBoardResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.MandalartHistoryResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.MandalartResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalBoardResponse;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalSnapshotRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MandalartQueryService {

    private final MandalartRepository mandalartRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final CoreGoalSnapshotRepository coreGoalSnapshotRepository;
    private final SubGoalRepository subGoalRepository;
    private final SubGoalSnapshotRepository subGoalSnapshotRepository;

    public MandalartHistoryResponse getMandalartHistory(Long userId, Long mandalartId) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);

        return MandalartHistoryResponse.of(mandalart, LocalDate.now());
    }

    public MandalartResponse getMandalart(Long userId, Long mandalartId) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);
        return MandalartResponse.of(mandalart);
    }

    public MandalartBoardResponse getMandalartBoard(Long userId, Long mandalartId) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);

        List<CoreGoalSnapshot> coreGoalSnapshots = coreGoalSnapshotRepository
            .findByMandalartIdOrderByPosition(mandalartId);

        List<CoreGoalBoardResponse> coreGoalBoardResponses = getAllCoreGoalsWithSubGoals(
            mandalartId,
            coreGoalSnapshots
        );

        return MandalartBoardResponse.of(mandalart, coreGoalBoardResponses);
    }

    private Mandalart getExistingMandalart(Long mandalartId) {
        return mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));
    }

    private List<CoreGoalBoardResponse> getAllCoreGoalsWithSubGoals(Long mandalartId,
        List<CoreGoalSnapshot> coreGoalSnapshots) {
        return coreGoalSnapshots.stream()
            .map(coreGoalSnapshot -> {
                List<SubGoalSnapshot> subGoalSnapshots =
                    subGoalSnapshotRepository
                        .findActiveSubGoalSnapshotFollowingCoreGoalOrderByPosition(
                            mandalartId,
                            coreGoalSnapshot.getId()
                        );

                List<SubGoalBoardResponse> subGoalBoardResponses = subGoalSnapshots.stream()
                    .map(SubGoalBoardResponse::from)
                    .toList();

                return CoreGoalBoardResponse.of(coreGoalSnapshot, subGoalBoardResponses);
            })
            .toList();
    }
}

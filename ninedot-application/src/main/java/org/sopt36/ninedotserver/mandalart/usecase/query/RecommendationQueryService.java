package org.sopt36.ninedotserver.mandalart.usecase.query;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.model.Mandalart;
import org.sopt36.ninedotserver.mandalart.model.Recommendation;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalDetailResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalListResponse;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.port.out.HistoryRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.MandalartRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.RecommendationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecommendationQueryService {

    private final RecommendationRepositoryPort recommendationRepository;
    private final MandalartRepositoryPort mandalartRepository;
    private final HistoryRepositoryPort historyRepository;

    public SubGoalListResponse getRecommendations(Long userId, Long mandalartId, LocalDate date) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);

        List<Recommendation> recommendations = recommendationRepository
            .findAllBySubGoalSnapshot_SubGoal_CoreGoal_Mandalart_IdAndRecommendationDate(
                mandalartId,
                date
            );

        List<SubGoalDetailResponse> subGoals = recommendations.stream()
            .map(Recommendation::getSubGoalSnapshot)
            .map(subGoalSnapshot -> {
                boolean isCompleted = historyRepository.existsBySubGoalSnapshotIdAndCompletedDate(
                    subGoalSnapshot.getId(), date
                );
                return SubGoalDetailResponse.of(subGoalSnapshot, isCompleted);
            })
            .collect(Collectors.toList());

        return SubGoalListResponse.of(subGoals);
    }

    private Mandalart getExistingMandalart(Long mandalartId) {
        return mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));
    }

}

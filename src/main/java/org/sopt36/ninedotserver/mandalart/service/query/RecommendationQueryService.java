package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.domain.Recommendation;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalDetailResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalListResponse;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.repository.HistoryRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.mandalart.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecommendationQueryService {

    private final RecommendationRepository recommendationRepository;
    private final MandalartRepository mandalartRepository;
    private final HistoryRepository historyRepository;

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

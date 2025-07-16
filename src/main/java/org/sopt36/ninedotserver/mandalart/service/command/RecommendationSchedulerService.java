package org.sopt36.ninedotserver.mandalart.service.command;

import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_NOT_FOUND;
import static org.sopt36.ninedotserver.user.exception.UserErrorCode.USER_NOT_FOUND;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sopt36.ninedotserver.mandalart.domain.Recommendation;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.dto.response.SubGoalDetailResponse;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.repository.HistoryRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.mandalart.repository.RecommendationRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalSnapshotRepository;
import org.sopt36.ninedotserver.user.domain.User;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecommendationSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationSchedulerService.class);

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final SubGoalSnapshotRepository subGoalSnapshotRepository;
    private final MandalartRepository mandalartRepository;
    private final HistoryRepository historyRepository;


    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void generateDailyRecommendations() {
        LocalDate today = LocalDate.now();

        userRepository.findAll().forEach(user -> {
            log.info("Executing generateDailyRecommendations()");

            Long userId = user.getId();
            List<Long> mandalartIds = mandalartRepository.findIdByUserId(userId);

            for (Long mandalartId : mandalartIds) {
                List<SubGoalDetailResponse> recs = computeRecommendations(userId, mandalartId);

                recs.forEach(dto -> {
                    SubGoalSnapshot snap =
                        subGoalSnapshotRepository.findById(dto.id())
                            .orElseThrow(() -> new SubGoalException(SUB_GOAL_NOT_FOUND));

                    Recommendation rec = Recommendation.create(user, snap, today);
                    recommendationRepository.save(rec);
                });
            }
        });
    }

    @Transactional
    public List<SubGoalDetailResponse> computeRecommendations(Long userId, Long mandalartId) {
        LocalDate today = LocalDate.now();

        // 1) 가입일 기준으로 filterDays 계산
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        long daysSinceSignup =
            ChronoUnit.DAYS.between(user.getCreatedAt().toLocalDate(), today) + 1;
        int filterDays = daysSinceSignup == 1 ? 0
            : daysSinceSignup == 2 ? 1
                : daysSinceSignup == 3 ? 2
                    : 3;

        LocalDate startDate = today.minusDays(filterDays);
        LocalDate endDate = today.minusDays(1);

        // 2) 해당 기간에 완료된 SubGoalSnapshot ID 조회
        List<Long> doneIds = filterDays > 0
            ? historyRepository.findCompletedSubGoalIdsByUser(userId, startDate, endDate)
            : Collections.emptyList();

        // 3) 만다라트에 속한 모든 SubGoalSnapshot 중 완료된 것 제외
        List<SubGoalSnapshot> candidates = subGoalSnapshotRepository
            .findAllByMandalartId(mandalartId).stream()
            .filter(s -> !doneIds.contains(s.getId()))
            .collect(Collectors.toList());

        // 4) 랜덤 섞고 최대 3개 선택
        Collections.shuffle(candidates);
        List<SubGoalSnapshot> picked = candidates.stream()
            .limit(3)
            .toList();

        // 5) Recommendation에 저장 & DTO 변환
        List<SubGoalDetailResponse> result = new ArrayList<>();
        for (SubGoalSnapshot snapshot : picked) {
            result.add(SubGoalDetailResponse.from(snapshot));
        }

        return result;
    }
}

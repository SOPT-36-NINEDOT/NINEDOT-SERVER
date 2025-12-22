package org.sopt36.ninedotserver.mandalart.usecase.command;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;
import static org.sopt36.ninedotserver.user.exception.UserErrorCode.USER_NOT_FOUND;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.model.Mandalart;
import org.sopt36.ninedotserver.mandalart.model.Recommendation;
import org.sopt36.ninedotserver.mandalart.model.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.port.out.HistoryRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.MandalartRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.RecommendationRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.SubGoalSnapshotRepositoryPort;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecommendationSchedulerService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final RecommendationRepositoryPort recommendationRepository;
    private final UserQueryPort userRepository;
    private final SubGoalSnapshotRepositoryPort subGoalSnapshotRepository;
    private final MandalartRepositoryPort mandalartRepository;
    private final HistoryRepositoryPort historyRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void generateDailyRecommendations() {
        userRepository.findAll().forEach(user -> {
            Long userId = user.getId();
            List<Long> mandalartIds = mandalartRepository.findIdByUserId(userId);

            for (Long mandalartId : mandalartIds) {
                computeRecommendations(userId, mandalartId);
            }
        });
    }

    @Transactional
    public void computeRecommendations(Long userId, Long mandalartId) {
        LocalDate today = LocalDate.now(KST);

        // 1) 가입일(만다라트 완성일) 기준으로 filterDays 계산
        Mandalart mandalart = mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        LocalDate completedDate =
            mandalart.getCompletedAt()
                .atZone(KST)
                .toLocalDate();

        long daysSinceCompletion =
            ChronoUnit.DAYS.between(completedDate, today) + 1;

        int filterDays =
            daysSinceCompletion == 1 ? 0 :
                daysSinceCompletion == 2 ? 1 :
                    daysSinceCompletion == 3 ? 2 : 3;

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
        for (SubGoalSnapshot snapshot : picked) {
            Recommendation rec = Recommendation.create(user, snapshot, today);
            recommendationRepository.save(rec);
        }
    }
}

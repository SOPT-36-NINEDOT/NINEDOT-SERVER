package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.History;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.dto.response.StreakListResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.StreakResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.TodoResponse;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.repository.HistoryRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class HistoryQueryService {

    private final HistoryRepository historyRepository;
    private final MandalartRepository mandalartRepository;

    public StreakListResponse getStreaks(Long userId, Long mandalartId) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);

        // 기간 계산
        LocalDate startDate = mandalart.getCreatedAt().toLocalDate();
        LocalDate today = LocalDate.now();
        long totalDays = ChronoUnit.DAYS.between(startDate, today) + 1;

        List<LocalDate> allDates = IntStream.range(0, (int) totalDays)
            .mapToObj(startDate::plusDays)
            .toList();

        // 전체 히스토리 조회
        List<History> allHistories = historyRepository
            .findBySubGoalSnapshot_SubGoal_CoreGoal_Mandalart_IdAndCompletedDateBetween(
                mandalartId,
                startDate,
                today
            );

        // 기간 전체 히스토리 조회 -> 날짜별 그룹핑
        Map<LocalDate, List<TodoResponse>> grouped = allHistories.stream()
            .collect(Collectors.groupingBy(
                History::getCompletedDate,
                Collectors.mapping(h -> new TodoResponse(
                    h.getSubGoalSnapshot().getId(),
                    h.getSubGoalSnapshot().getTitle()
                ), Collectors.toList())
            ));

        // 스트릭 리스트 생성
        List<StreakResponse> entries = allDates.stream()
            .map(date -> {
                List<TodoResponse> todos = grouped.getOrDefault(date, List.of());
                int streakDay = (int) ChronoUnit.DAYS.between(startDate, date) + 1;
                return new StreakResponse(
                    streakDay,
                    todos.size(),
                    todos
                );
            })
            .toList();

        return StreakListResponse.of(entries);
    }

    private Mandalart getExistingMandalart(Long mandalartId) {
        return mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));
    }

}

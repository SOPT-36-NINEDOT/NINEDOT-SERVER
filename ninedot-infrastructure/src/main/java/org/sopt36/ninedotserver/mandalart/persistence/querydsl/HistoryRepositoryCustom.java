package org.sopt36.ninedotserver.mandalart.persistence.querydsl;

import java.time.LocalDate;
import java.util.List;

public interface HistoryRepositoryCustom {

    List<Long> findCompletedSubGoalIdsByUser(Long userId, LocalDate start, LocalDate end);

}

package org.sopt36.ninedotserver.mandalart.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.QCoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.QHistory;
import org.sopt36.ninedotserver.mandalart.domain.QMandalart;
import org.sopt36.ninedotserver.mandalart.domain.QSubGoal;
import org.sopt36.ninedotserver.mandalart.domain.QSubGoalSnapshot;

@RequiredArgsConstructor
public class HistoryRepositoryImpl implements HistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findCompletedSubGoalIdsByUser(
        Long userId, LocalDate startDate, LocalDate endDate
    ) {
        QHistory h = QHistory.history;
        QSubGoalSnapshot s = QSubGoalSnapshot.subGoalSnapshot;
        QSubGoal sg = QSubGoal.subGoal;
        QCoreGoal cg = QCoreGoal.coreGoal;
        QMandalart m = QMandalart.mandalart;

        return queryFactory
            .select(h.subGoalSnapshot.id)
            .from(h)
            .join(h.subGoalSnapshot, s)
            .join(s.subGoal, sg)
            .join(sg.coreGoal, cg)
            .join(cg.mandalart, m)
            .where(
                m.user.id.eq(userId),
                h.completedDate.between(startDate, endDate)
            )
            .fetch();
    }
}

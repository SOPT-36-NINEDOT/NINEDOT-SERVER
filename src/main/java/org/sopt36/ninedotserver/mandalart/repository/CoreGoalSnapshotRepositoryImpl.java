package org.sopt36.ninedotserver.mandalart.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.QCoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.QCoreGoalSnapshot;

@RequiredArgsConstructor
public class CoreGoalSnapshotRepositoryImpl implements CoreGoalSnapshotRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CoreGoalSnapshot> findByMandalartIdOrderByPosition(Long mandalartId) {
        QCoreGoalSnapshot snapshot = QCoreGoalSnapshot.coreGoalSnapshot;
        QCoreGoal coreGoal = QCoreGoal.coreGoal;

        return queryFactory
            .selectFrom(snapshot)
            .join(snapshot.coreGoal, coreGoal).fetchJoin()
            .where(coreGoal.mandalart.id.eq(mandalartId))
            .orderBy(coreGoal.position.asc())
            .fetch();
    }

    @Override
    public List<String> findActiveCoreGoalTitleByMandalartId(Long mandalartId) {
        QCoreGoalSnapshot snapshot = QCoreGoalSnapshot.coreGoalSnapshot;

        return queryFactory
            .select(snapshot.title)
            .from(snapshot)
            .where(snapshot.coreGoal.mandalart.id.eq(mandalartId)
                .and(snapshot.validTo.isNull()))
            .fetch();
    }

}

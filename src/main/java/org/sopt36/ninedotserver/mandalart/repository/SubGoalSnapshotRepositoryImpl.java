package org.sopt36.ninedotserver.mandalart.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.QCoreGoal;
import org.sopt36.ninedotserver.mandalart.domain.QCoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.QSubGoal;
import org.sopt36.ninedotserver.mandalart.domain.QSubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.domain.SubGoalSnapshot;

@RequiredArgsConstructor
public class SubGoalSnapshotRepositoryImpl implements SubGoalSnapshotRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SubGoalSnapshot> findByCoreGoalSnapshotIdOrderByPosition(Long coreGoalSnapshotId) {
        QSubGoalSnapshot subGoalSnapshot = QSubGoalSnapshot.subGoalSnapshot;
        QSubGoal subGoal = QSubGoal.subGoal;
        QCoreGoal coreGoal = QCoreGoal.coreGoal;

        return queryFactory
            .selectFrom(subGoalSnapshot)
            .join(subGoalSnapshot.subGoal, subGoal).fetchJoin()
            .join(subGoal.coreGoal, coreGoal).fetchJoin()
            .where(coreGoal.id.eq(
                queryFactory.select(QCoreGoalSnapshot.coreGoalSnapshot.coreGoal.id)
                    .from(QCoreGoalSnapshot.coreGoalSnapshot)
                    .where(QCoreGoalSnapshot.coreGoalSnapshot.id.eq(coreGoalSnapshotId))
            ))
            .orderBy(subGoalSnapshot.subGoal.position.asc())
            .fetch();
    }
}

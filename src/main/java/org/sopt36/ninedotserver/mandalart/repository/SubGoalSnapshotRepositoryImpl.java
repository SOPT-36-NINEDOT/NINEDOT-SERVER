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

    @Override
    public List<SubGoalSnapshot> findAllByMandalartId(Long mandalartId) {
        QSubGoalSnapshot s = QSubGoalSnapshot.subGoalSnapshot;
        QSubGoal sg = QSubGoal.subGoal;
        QCoreGoal cg = QCoreGoal.coreGoal;

        return queryFactory
            .selectFrom(s)
            .join(s.subGoal, sg)
            .join(sg.coreGoal, cg)
            .where(cg.mandalart.id.eq(mandalartId))
            .fetch();
    }
}

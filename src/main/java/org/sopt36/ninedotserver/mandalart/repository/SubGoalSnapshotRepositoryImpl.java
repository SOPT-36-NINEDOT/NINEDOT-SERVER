package org.sopt36.ninedotserver.mandalart.repository;

import static org.sopt36.ninedotserver.mandalart.domain.QCoreGoal.coreGoal;
import static org.sopt36.ninedotserver.mandalart.domain.QCoreGoalSnapshot.coreGoalSnapshot;
import static org.sopt36.ninedotserver.mandalart.domain.QSubGoal.subGoal;
import static org.sopt36.ninedotserver.mandalart.domain.QSubGoalSnapshot.subGoalSnapshot;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.Cycle;
import org.sopt36.ninedotserver.mandalart.domain.QCoreGoal;
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
                queryFactory.select(coreGoalSnapshot.coreGoal.id)
                    .from(coreGoalSnapshot)
                    .where(coreGoalSnapshot.id.eq(coreGoalSnapshotId))
            ))
            .orderBy(subGoalSnapshot.subGoal.position.asc())
            .fetch();
    }

    @Override
    public List<SubGoalSnapshot> findAllByMandalartId(Long mandalartId) {
        QSubGoalSnapshot s = subGoalSnapshot;
        QSubGoal sg = subGoal;
        QCoreGoal cg = coreGoal;

        return queryFactory
            .selectFrom(s)
            .join(s.subGoal, sg)
            .join(sg.coreGoal, cg)
            .where(cg.mandalart.id.eq(mandalartId))
            .fetch();
    }

    @Override
    public List<SubGoalSnapshot> findAllActiveSubGoalSnapshot(Long mandalartId) {
        return queryFactory
            .selectFrom(subGoalSnapshot)
            .join(subGoalSnapshot.subGoal, subGoal)
            .join(subGoal.coreGoal, coreGoal)
            .where(coreGoal.mandalart.id.eq(mandalartId).and(subGoalSnapshot.validTo.isNull()))
            .fetch();
    }

    @Override
    public List<SubGoalSnapshot> findActiveSubGoalSnapshotByCycle(Long mandalartId, Cycle cycle) {
        return queryFactory
            .selectFrom(subGoalSnapshot)
            .join(subGoalSnapshot.subGoal, subGoal)
            .join(subGoal.coreGoal, coreGoal)
            .where(coreGoal.mandalart.id.eq(mandalartId)
                .and(subGoalSnapshot.validTo.isNull())
                .and(subGoalSnapshot.cycle.eq(cycle)))
            .fetch();
    }

    @Override
    public List<SubGoalSnapshot> findActiveSubGoalSnapshotFollowingCoreGoal(Long mandalartId,
        Long coreGoalSnapshotId) {
        return queryFactory
            .selectFrom(subGoalSnapshot)
            .join(subGoalSnapshot.subGoal, subGoal)
            .join(subGoal.coreGoal, coreGoal)
            .join(coreGoalSnapshot).on(coreGoalSnapshot.coreGoal.eq(coreGoal))
            .where(coreGoal.mandalart.id.eq(mandalartId)
                .and(coreGoalSnapshot.id.eq(coreGoalSnapshotId))
                .and(subGoalSnapshot.validTo.isNull()))
            .fetch();
    }

    @Override
    public List<SubGoalSnapshot> findActiveSubGoalSnapshotFollowingCycleAndCoreGoal(
        Long mandalartId, Long coreGoalSnapshotId, Cycle cycle) {
        return queryFactory
            .selectFrom(subGoalSnapshot)
            .join(subGoalSnapshot.subGoal, subGoal)
            .join(subGoal.coreGoal, coreGoal)
            .join(coreGoalSnapshot).on(coreGoalSnapshot.coreGoal.eq(coreGoal))
            .where(coreGoal.mandalart.id.eq(mandalartId)
                .and(coreGoalSnapshot.id.eq(coreGoalSnapshotId))
                .and(subGoalSnapshot.cycle.eq(cycle))
                .and(subGoalSnapshot.validTo.isNull()))
            .fetch();
    }

    @Override
    public List<SubGoalSnapshot> findActiveSubGoalSnapshotFollowingCoreGoalOrderByPosition(
        Long mandalartId, Long coreGoalSnapshotId) {
        return queryFactory
            .selectFrom(subGoalSnapshot)
            .join(subGoalSnapshot.subGoal, subGoal)
            .join(subGoal.coreGoal, coreGoal)
            .join(coreGoalSnapshot).on(coreGoalSnapshot.coreGoal.eq(coreGoal))
            .where(coreGoal.mandalart.id.eq(mandalartId)
                .and(coreGoalSnapshot.id.eq(coreGoalSnapshotId))
                .and(subGoalSnapshot.validTo.isNull()))
            .orderBy(subGoal.position.asc())
            .fetch();
    }

    @Override
    public int countActiveSubGoalSnapshotByCoreGoal(Long coreGoalId) {
        Long count = queryFactory
            .select(subGoalSnapshot.count())
            .from(subGoalSnapshot)
            .join(subGoalSnapshot.subGoal, subGoal)
            .join(subGoal.coreGoal, coreGoal)
            .where(
                coreGoal.id.eq(coreGoalId)
                    .and(subGoalSnapshot.validTo.isNull())
            )
            .fetchOne();

        return Math.toIntExact(count != null ? count : 0L);
    }

    @Override
    public List<Integer> findActiveSubGoalPositionsByCoreGoal(Long coreGoalId) {
        return queryFactory
            .select(subGoal.position)
            .from(subGoalSnapshot)
            .join(subGoalSnapshot.subGoal, subGoal)
            .join(subGoal.coreGoal, coreGoal)
            .join(coreGoalSnapshot).on(coreGoalSnapshot.coreGoal.eq(coreGoal))
            .where(coreGoalSnapshot.id.eq(coreGoalId)
                .and(subGoalSnapshot.validTo.isNull()))
            .fetch();
    }
}

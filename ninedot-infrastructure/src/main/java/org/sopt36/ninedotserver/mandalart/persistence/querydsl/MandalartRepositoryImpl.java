package org.sopt36.ninedotserver.mandalart.persistence.querydsl;

import static org.sopt36.ninedotserver.mandalart.model.QCoreGoal.coreGoal;
import static org.sopt36.ninedotserver.mandalart.model.QMandalart.mandalart;
import static org.sopt36.ninedotserver.mandalart.model.QCoreGoalSnapshot.coreGoalSnapshot;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.user.model.User;

@RequiredArgsConstructor
public class MandalartRepositoryImpl implements MandalartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserId(Long userId) {
        Integer found = queryFactory
            .selectOne()
            .from(mandalart)
            .where(mandalart.user.id.eq(userId))
            .fetchFirst();
        return found != null;
    }

    @Override
    public Optional<User> findUserById(Long mandalartId) {
        User result = queryFactory
            .select(mandalart.user)
            .from(mandalart)
            .where(mandalart.id.eq(mandalartId))
            .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<String> findTitleByCoreGoalId(Long coreGoalSnapshotId) {
        return Optional.ofNullable(
            queryFactory
                .select(mandalart.title)
                .from(coreGoal)
                .join(coreGoal.mandalart, mandalart)
                .join(coreGoalSnapshot).on(coreGoalSnapshot.coreGoal.eq(coreGoal))
                .where(coreGoalSnapshot.id.eq(coreGoalSnapshotId))
                .fetchOne()
        );
    }

}

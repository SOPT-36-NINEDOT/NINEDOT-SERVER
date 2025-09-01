package org.sopt36.ninedotserver.mandalart.persistence.querydsl;

import static org.sopt36.ninedotserver.mandalart.model.QCoreGoal.coreGoal;
import static org.sopt36.ninedotserver.mandalart.model.QMandalart.mandalart;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoreGoalRepositoryImpl implements CoreGoalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserId(Long userId) {
        Integer found = queryFactory
            .selectOne()
            .from(coreGoal)
            .join(coreGoal.mandalart, mandalart)
            .where(mandalart.user.id.eq(userId))
            .fetchFirst();
        return found != null;
    }
}

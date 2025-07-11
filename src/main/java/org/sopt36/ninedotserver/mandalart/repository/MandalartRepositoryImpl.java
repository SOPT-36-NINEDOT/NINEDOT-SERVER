package org.sopt36.ninedotserver.mandalart.repository;

import static org.sopt36.ninedotserver.mandalart.domain.QCoreGoal.coreGoal;
import static org.sopt36.ninedotserver.mandalart.domain.QMandalart.mandalart;
import static org.sopt36.ninedotserver.mandalart.domain.QSubGoal.subGoal;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

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
}

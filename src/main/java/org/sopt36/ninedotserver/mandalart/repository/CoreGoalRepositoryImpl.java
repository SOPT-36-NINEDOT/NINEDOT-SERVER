package org.sopt36.ninedotserver.mandalart.repository;

import static org.sopt36.ninedotserver.mandalart.domain.QCoreGoal.coreGoal;
import static org.sopt36.ninedotserver.mandalart.domain.QMandalart.mandalart;
import static org.sopt36.ninedotserver.mandalart.domain.QSubGoal.subGoal;
import static org.sopt36.ninedotserver.onboarding.domain.Domain.JOB;
import static org.sopt36.ninedotserver.onboarding.domain.QChoice.choice;
import static org.sopt36.ninedotserver.onboarding.domain.QQuestion.question;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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

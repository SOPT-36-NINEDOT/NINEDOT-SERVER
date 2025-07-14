package org.sopt36.ninedotserver.mandalart.repository;

import static org.sopt36.ninedotserver.mandalart.domain.QCoreGoal.coreGoal;
import static org.sopt36.ninedotserver.mandalart.domain.QMandalart.mandalart;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.user.domain.User;

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
    public Optional<String> findTitleByMandalartId(Long mandalartId) {
        String title = queryFactory
            .select(mandalart.title)
            .from(mandalart)
            .where(mandalart.id.eq(mandalartId))
            .fetchOne();
        return Optional.ofNullable(title);
    }

    @Override
    public Optional<String> findTitleByCoreGoalId(Long coreGoalId) {
        return Optional.ofNullable(
            queryFactory
                .select(mandalart.title)
                .from(coreGoal)
                .join(coreGoal.mandalart, mandalart)
                .where(coreGoal.id.eq(coreGoalId))
                .fetchOne()
        );
    }

}

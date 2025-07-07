package org.sopt36.ninedotserver.mandalart.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubGoalRepositoryImpl implements SubGoalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

}

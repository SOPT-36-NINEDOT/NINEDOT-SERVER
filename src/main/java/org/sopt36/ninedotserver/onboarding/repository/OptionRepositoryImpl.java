package org.sopt36.ninedotserver.onboarding.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

}

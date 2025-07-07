package org.sopt36.ninedotserver.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl {

    private final JPAQueryFactory queryFactory;

}

package org.sopt36.ninedotserver.auth.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthProviderRepositoryImpl {

    private final JPAQueryFactory queryFactory;

}

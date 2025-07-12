package org.sopt36.ninedotserver.onboarding.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.onboarding.domain.Domain;
import org.sopt36.ninedotserver.onboarding.domain.QAnswer;
import org.sopt36.ninedotserver.onboarding.domain.Question;

@RequiredArgsConstructor
public class AnswerRepositoryImpl implements AnswerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<Question> findQuestionsByUserIdAndDomain(Long userId, Domain domain) {
        QAnswer a = QAnswer.answer;

        return queryFactory
                   .select(a.question)
                   .from(a)
                   .where(
                       a.user.id.eq(userId),
                       a.question.domain.eq(domain)
                   )
                   .fetch();
    }
}

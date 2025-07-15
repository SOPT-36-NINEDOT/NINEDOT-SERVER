package org.sopt36.ninedotserver.onboarding.repository;

import static org.sopt36.ninedotserver.onboarding.domain.QQuestion.question;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.onboarding.domain.QQuestion;
import org.sopt36.ninedotserver.onboarding.domain.Question;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Question> findAllActivated() {
        return queryFactory
            .selectFrom(question)
            .where(question.activated.isTrue())
            .fetch();
    }

}

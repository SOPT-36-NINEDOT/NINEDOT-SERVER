package org.sopt36.ninedotserver.onboarding.persistence.querydsl;

import static org.sopt36.ninedotserver.onboarding.model.Domain.JOB;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.onboarding.model.Choice;
import org.sopt36.ninedotserver.onboarding.model.QChoice;
import org.sopt36.ninedotserver.onboarding.model.QQuestion;

@RequiredArgsConstructor
public class ChoiceRepositoryImpl implements ChoiceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Choice> findJobList() {
        return queryFactory
            .select(QChoice.choice)
            .from(QChoice.choice).join(QChoice.choice.question, QQuestion.question)
            .where(QQuestion.question.domain.eq(JOB), QChoice.choice.activated.isTrue())
            .fetch();
    }

}

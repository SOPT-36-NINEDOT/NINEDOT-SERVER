package org.sopt36.ninedotserver.onboarding.repository;

import static org.sopt36.ninedotserver.onboarding.domain.Domain.JOB;
import static org.sopt36.ninedotserver.onboarding.domain.QChoice.choice;
import static org.sopt36.ninedotserver.onboarding.domain.QQuestion.question;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.onboarding.domain.Choice;

@RequiredArgsConstructor
public class ChoiceRepositoryImpl implements ChoiceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Choice> findJobList() {
        return queryFactory
                   .select(choice)
                   .from(choice).join(choice.question, question)
                   .where(question.domain.eq(JOB), choice.activated.isTrue())
                   .fetch();
    }

}

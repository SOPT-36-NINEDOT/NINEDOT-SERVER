package org.sopt36.ninedotserver.onboarding.repository;

import static org.sopt36.ninedotserver.onboarding.domain.QAnswer.answer;
import static org.sopt36.ninedotserver.onboarding.domain.QQuestion.question;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.onboarding.domain.Domain;
import org.sopt36.ninedotserver.onboarding.domain.QAnswer;
import org.sopt36.ninedotserver.onboarding.domain.Question;

@RequiredArgsConstructor
public class AnswerRepositoryImpl implements AnswerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<Question> findQuestionsByUserIdAndDomain(Long userId, Domain domain) {
        QAnswer a = answer;

        return queryFactory
            .select(a.question)
            .from(a)
            .where(
                a.user.id.eq(userId),
                a.question.domain.eq(domain)
            )
            .fetch();
    }

    @Override
    public Map<String, String> findQnAMapByUserId(Long userId) {
        List<Tuple> results = queryFactory
            .select(question.content, answer.content)
            .from(answer)
            .join(answer.question, question)
            .where(answer.user.id.eq(userId))
            .fetch();

        return results.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(question.content),
                tuple -> tuple.get(answer.content)
            ));
    }
}

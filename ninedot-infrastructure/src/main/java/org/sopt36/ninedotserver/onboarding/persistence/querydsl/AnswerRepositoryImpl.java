package org.sopt36.ninedotserver.onboarding.persistence.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.onboarding.model.Domain;
import org.sopt36.ninedotserver.onboarding.model.QAnswer;
import org.sopt36.ninedotserver.onboarding.model.QQuestion;
import org.sopt36.ninedotserver.onboarding.model.Question;

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

    @Override
    public Map<String, String> findQnAMapByUserId(Long userId) {
        List<Tuple> results = queryFactory
            .select(QQuestion.question.content, QAnswer.answer.content)
            .from(QAnswer.answer)
            .join(QAnswer.answer.question, QQuestion.question)
            .where(QAnswer.answer.user.id.eq(userId))
            .fetch();

        return results.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(QQuestion.question.content),
                tuple -> tuple.get(QAnswer.answer.content)
            ));
    }
}

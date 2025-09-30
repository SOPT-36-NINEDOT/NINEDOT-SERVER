package org.sopt36.ninedotserver.auth.service.signup;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.dto.command.SignupCommand;
import org.sopt36.ninedotserver.auth.dto.result.SignupThenLoginResult;
import org.sopt36.ninedotserver.auth.dto.token.IssuedTokens;
import org.sopt36.ninedotserver.auth.model.AuthProvider;
import org.sopt36.ninedotserver.auth.model.ProviderType;
import org.sopt36.ninedotserver.auth.port.in.SignupUsecase;
import org.sopt36.ninedotserver.auth.port.out.AuthProviderRepositoryPort;
import org.sopt36.ninedotserver.auth.service.token.TokenService;
import org.sopt36.ninedotserver.onboarding.exception.QuestionErrorCode;
import org.sopt36.ninedotserver.onboarding.exception.QuestionException;
import org.sopt36.ninedotserver.onboarding.model.Answer;
import org.sopt36.ninedotserver.onboarding.model.ChoiceInfo;
import org.sopt36.ninedotserver.onboarding.model.Question;
import org.sopt36.ninedotserver.onboarding.port.out.AnswerRepositoryPort;
import org.sopt36.ninedotserver.onboarding.port.out.QuestionRepositoryPort;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.port.out.UserCommandPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SignupHandler implements SignupUsecase {

    private final UserCommandPort userCommandPort;
    private final AnswerRepositoryPort answerRepositoryPort;
    private final QuestionRepositoryPort questionRepositoryPort;
    private final AuthProviderRepositoryPort authProviderRepositoryPort;
    private final TokenService tokenService;

    @Transactional
    public SignupThenLoginResult execute(SignupCommand signupCommand) {
        User user = User.create(
            signupCommand.name(),
            signupCommand.email(),
            signupCommand.profileImageUrl(),
            signupCommand.birthday(),
            signupCommand.job()
        );
        userCommandPort.saveAndFlush(user);

        List<Answer> answers = getAnswers(signupCommand, user);
        answerRepositoryPort.saveAll(answers);

        AuthProvider authProvider = AuthProvider.create(user,
            ProviderType.valueOf(signupCommand.socialProvider().toUpperCase()),
            signupCommand.socialToken());
        authProviderRepositoryPort.save(authProvider);

        IssuedTokens issuedTokens = tokenService.issueTokens(user.getId());

        return SignupThenLoginResult.of(issuedTokens, user);
    }

    private List<Answer> getAnswers(SignupCommand signupCommand, User user) {
        return signupCommand.answers()
            .stream()
            .map(answer -> {
                Question question = questionRepositoryPort.findById(answer.questionId())
                    .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));
                String content = ChoiceInfo.getShortSentenceById(answer.choiceId());
                return Answer.create(question, user, content);
            })
            .collect(Collectors.toList());
    }
}

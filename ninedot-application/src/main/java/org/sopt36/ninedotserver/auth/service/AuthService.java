package org.sopt36.ninedotserver.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.auth.dto.command.SignupCommand;
import org.sopt36.ninedotserver.auth.dto.response.NewAccessTokenResult;
import org.sopt36.ninedotserver.auth.dto.response.SignupResult;
import org.sopt36.ninedotserver.auth.dto.security.TokenClaims;
import org.sopt36.ninedotserver.auth.exception.AuthErrorCode;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.model.AuthProvider;
import org.sopt36.ninedotserver.auth.model.ProviderType;
import org.sopt36.ninedotserver.auth.model.RefreshToken;
import org.sopt36.ninedotserver.auth.port.out.AuthProviderRepositoryPort;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenRepositoryPort;
import org.sopt36.ninedotserver.auth.port.out.token.TokenIssuePort;
import org.sopt36.ninedotserver.auth.port.out.token.TokenParsePort;
import org.sopt36.ninedotserver.onboarding.exception.QuestionErrorCode;
import org.sopt36.ninedotserver.onboarding.exception.QuestionException;
import org.sopt36.ninedotserver.onboarding.model.Answer;
import org.sopt36.ninedotserver.onboarding.model.ChoiceInfo;
import org.sopt36.ninedotserver.onboarding.model.Question;
import org.sopt36.ninedotserver.onboarding.port.out.AnswerRepositoryPort;
import org.sopt36.ninedotserver.onboarding.port.out.QuestionRepositoryPort;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.port.out.UserCommandPort;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthService {

    private final TokenIssuePort tokenIssuePort;
    private final TokenParsePort tokenParsePort;
    private final AuthProviderRepositoryPort authProviderRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserQueryPort userQueryPort;
    private final UserCommandPort userCommandPort;
    private final AnswerRepositoryPort answerRepository;
    private final QuestionRepositoryPort questionRepository;

    @Value("${jwt.access-token-expiration-milliseconds}")
    long accessTokenExpirationMilliseconds;

    @Value("${jwt.refresh-token-expiration-milliseconds}")
    long refreshTokenExpirationMilliseconds;

    public AuthService(TokenIssuePort tokenIssuePort, TokenParsePort tokenParsePort,
        AuthProviderRepositoryPort authProviderRepository,
        RefreshTokenRepositoryPort refreshTokenRepository, UserQueryPort userQueryPort,
        UserCommandPort userCommandPort, AnswerRepositoryPort answerRepository,
        QuestionRepositoryPort questionRepository) {
        this.tokenIssuePort = tokenIssuePort;
        this.tokenParsePort = tokenParsePort;
        this.authProviderRepository = authProviderRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userQueryPort = userQueryPort;
        this.userCommandPort = userCommandPort;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public NewAccessTokenResult createNewAccessToken(String refreshToken) {
        RefreshToken rt = isRefreshTokenValid(refreshToken);

        Long userId = rt.getUser().getId();
        String newAccessToken = tokenIssuePort.createToken(userId,
            accessTokenExpirationMilliseconds);

        refreshTokenRepository.delete(rt);
        generateAndStoreRefreshToken(userId);
        return new NewAccessTokenResult(newAccessToken, "새로운 액세스토큰이 생성되었습니다.");
    }

    @Transactional
    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public SignupResult registerUser(SignupCommand request) {
        User user = User.create(request.name(), request.email(), request.profileImageUrl(),
            request.birthday(), request.job());
        userCommandPort.save(user);

        List<Answer> answers = getAnswers(request, user);
        answerRepository.saveAll(answers);

        AuthProvider authProvider = AuthProvider.create(user,
            ProviderType.valueOf(request.socialProvider().toUpperCase()), request.socialToken());
        authProviderRepository.save(authProvider);

        String accessToken = tokenIssuePort.createToken(user.getId(),
            accessTokenExpirationMilliseconds);
        generateAndStoreRefreshToken(user.getId());

        return SignupResult.of(accessToken, user);
    }

    private String generateAndStoreRefreshToken(Long userId) {
        String refreshToken = tokenIssuePort.createToken(userId,
            refreshTokenExpirationMilliseconds);
        //ㄴ토큰을 만들어
        addRefreshTokenToDB(userId, refreshToken);
        //ㄴdb에 refresh token 추가
        return refreshToken;
    }

    private RefreshToken isRefreshTokenValid(String refreshToken) {
        return refreshTokenRepository.findByRefreshTokenAndExpiresAtAfter(refreshToken,
                LocalDateTime.now())
            .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN));
    }

    private void addRefreshTokenToDB(Long userId, String refreshToken) {
        TokenClaims claims = tokenParsePort.parseClaims(refreshToken);
        User user = userQueryPort.findById(userId)
            .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.save(RefreshToken.create(user, refreshToken,
            claims.expiresAt().atZone(ZoneId.systemDefault()).toLocalDateTime()));
    }

    private List<Answer> getAnswers(SignupCommand request, User user) {
        return request.answers().stream().map(answer -> {
            Question question = questionRepository.findById(answer.questionId())
                .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));
            String content = ChoiceInfo.getShortSentenceById(answer.choiceId());
            return Answer.create(question, user, content);
        }).collect(Collectors.toList());
    }
}

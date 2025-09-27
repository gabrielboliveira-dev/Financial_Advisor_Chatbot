package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.QuizQuestion;
import br.com.financialchatbot.backend.domain.entities.RiskProfile;
import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.UserGateway;
import br.com.financialchatbot.backend.domain.services.QuizService;
import br.com.financialchatbot.backend.infrastructure.cache.QuizStateCache;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProcessQuizResponseUseCase {

    private final QuizService quizService;
    private final QuizStateCache quizStateCache;
    private final UserGateway userGateway;

    public record Output(String responseText, boolean isQuizOver) {}

    public ProcessQuizResponseUseCase(QuizService quizService, QuizStateCache quizStateCache, UserGateway userGateway) {
        this.quizService = quizService;
        this.quizStateCache = quizStateCache;
        this.userGateway = userGateway;
    }

    public Output execute(long chatId, String userAnswer) {
        Optional<QuizStateCache.QuizState> quizStateOpt = quizStateCache.findByChatId(chatId);

        if (quizStateOpt.isEmpty()) {
            return startQuiz(chatId);
        }

        QuizStateCache.QuizState state = quizStateOpt.get();
        QuizQuestion currentQuestion = quizService.getQuestionById(state.getCurrentQuestionId())
                .orElseThrow();

        Integer score = currentQuestion.options().get(userAnswer);
        if (score == null) {
            return new Output("Resposta inválida. Por favor, escolha uma das opções.", false);
        }

        quizStateCache.updateUserScore(chatId, score);

        if (state.getCurrentQuestionId() > quizService.getTotalQuestions()) {
            return finishQuiz(chatId, state.getScore());
        } else {
            QuizQuestion nextQuestion = quizService.getQuestionById(state.getCurrentQuestionId()).orElseThrow();
            return new Output(formatQuestion(nextQuestion), false);
        }
    }

    private Output startQuiz(long chatId) {
        quizStateCache.startQuiz(chatId);
        QuizQuestion firstQuestion = quizService.getQuestionById(1).orElseThrow();
        return new Output(formatQuestion(firstQuestion), false);
    }

    private Output finishQuiz(long chatId, int finalScore) {
        RiskProfile profile = quizService.calculateProfile(finalScore);

        User user = userGateway.findByChatId(chatId).orElseThrow();
        User updatedUser = new User(user.id(), user.chatId(), user.firstName(), profile);
        userGateway.save(updatedUser);

        quizStateCache.endQuiz(chatId);

        String resultText = String.format("Quiz finalizado! Seu perfil de investidor é: **%s**.", profile.name());
        return new Output(resultText, true);
    }

    private String formatQuestion(QuizQuestion question) {
        StringBuilder sb = new StringBuilder();
        sb.append(question.text()).append("\n\n");
        question.getOptionTexts().forEach(option -> sb.append("- ").append(option).append("\n"));
        return sb.toString();
    }
}
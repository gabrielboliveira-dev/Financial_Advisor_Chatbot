package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.QuizQuestion;
import br.com.financialchatbot.backend.domain.entities.RiskProfile;
import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.UserGateway;
import br.com.financialchatbot.backend.domain.services.QuizService;
import br.com.financialchatbot.backend.infrastructure.cache.QuizStateCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessQuizResponseUseCaseTest {

    @Mock
    private QuizService quizService;

    @Mock
    private QuizStateCache quizStateCache;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private ProcessQuizResponseUseCase useCase;

    private final Long chatId = 123L;

    @Test
    void execute_NoActiveQuiz_StartsQuiz() {
        when(quizStateCache.findByChatId(chatId)).thenReturn(Optional.empty());
        QuizQuestion firstQuestion = new QuizQuestion(1, "Q1", Map.of("A", 1, "B", 2));
        when(quizService.getQuestionById(1)).thenReturn(Optional.of(firstQuestion));

        ProcessQuizResponseUseCase.Output output = useCase.execute(chatId, "anything");

        assertFalse(output.isQuizOver());
        assertTrue(output.responseText().contains("Q1"));
        verify(quizStateCache).startQuiz(chatId);
    }

    @Test
    void execute_ActiveQuizInvalidAnswer_ReturnsInvalidMessage() {
        QuizStateCache.QuizState state = new QuizStateCache.QuizState(1, 0);
        when(quizStateCache.findByChatId(chatId)).thenReturn(Optional.of(state));
        QuizQuestion question = new QuizQuestion(1, "Q1", Map.of("A", 1, "B", 2));
        when(quizService.getQuestionById(1)).thenReturn(Optional.of(question));

        ProcessQuizResponseUseCase.Output output = useCase.execute(chatId, "C");

        assertFalse(output.isQuizOver());
        assertEquals("Resposta inválida. Por favor, escolha uma das opções.", output.responseText());
    }

    @Test
    void execute_ActiveQuizValidAnswerNotLastQuestion_AdvancesQuiz() {
        QuizStateCache.QuizState state = new QuizStateCache.QuizState(1, 0);
        when(quizStateCache.findByChatId(chatId)).thenReturn(Optional.of(state));
        QuizQuestion question = new QuizQuestion(1, "Q1", Map.of("A", 1, "B", 2));
        when(quizService.getQuestionById(1)).thenReturn(Optional.of(question));
        when(quizService.getTotalQuestions()).thenReturn(2);
        
        // Simular o comportamento do cache (que no mock precisaria atualizar o state, mas aqui o código do caso de uso checa state.getCurrentQuestionId() dnv.
        // O caso de uso atual acessa o mesmo objeto state e espera que o updateUserScore altere ele.
        doAnswer(invocation -> {
            state.setCurrentQuestionId(state.getCurrentQuestionId() + 1);
            state.setScore(state.getScore() + 1);
            return null;
        }).when(quizStateCache).updateUserScore(chatId, 1);

        QuizQuestion nextQuestion = new QuizQuestion(2, "Q2", Map.of("C", 1, "D", 2));
        when(quizService.getQuestionById(2)).thenReturn(Optional.of(nextQuestion));

        ProcessQuizResponseUseCase.Output output = useCase.execute(chatId, "A");

        assertFalse(output.isQuizOver());
        assertTrue(output.responseText().contains("Q2"));
        verify(quizStateCache).updateUserScore(chatId, 1);
    }

    @Test
    void execute_ActiveQuizValidAnswerLastQuestion_FinishesQuiz() {
        QuizStateCache.QuizState state = new QuizStateCache.QuizState(1, 0);
        when(quizStateCache.findByChatId(chatId)).thenReturn(Optional.of(state));
        QuizQuestion question = new QuizQuestion(1, "Q1", Map.of("A", 1, "B", 2));
        when(quizService.getQuestionById(1)).thenReturn(Optional.of(question));
        when(quizService.getTotalQuestions()).thenReturn(1);

        doAnswer(invocation -> {
            state.setCurrentQuestionId(state.getCurrentQuestionId() + 1);
            state.setScore(state.getScore() + 1);
            return null;
        }).when(quizStateCache).updateUserScore(chatId, 1);

        when(quizService.calculateProfile(anyInt())).thenReturn(RiskProfile.CONSERVADOR);
        when(userGateway.findByChatId(chatId)).thenReturn(Optional.of(new User(1L, chatId, "User", RiskProfile.NAO_DEFINIDO)));

        ProcessQuizResponseUseCase.Output output = useCase.execute(chatId, "A");

        assertTrue(output.isQuizOver());
        assertTrue(output.responseText().contains("CONSERVADOR"));
        verify(quizStateCache).endQuiz(chatId);
        verify(userGateway).save(any(User.class));
    }
}
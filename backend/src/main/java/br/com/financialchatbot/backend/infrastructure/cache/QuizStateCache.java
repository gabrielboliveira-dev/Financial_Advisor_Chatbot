package br.com.financialchatbot.backend.infrastructure.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QuizStateCache {

    private final Map<Long, QuizState> userQuizState = new ConcurrentHashMap<>();

    public void startQuiz(Long chatId) {
        userQuizState.put(chatId, new QuizState());
    }

    public Optional<QuizState> findByChatId(Long chatId) {
        return Optional.ofNullable(userQuizState.get(chatId));
    }

    public void updateUserScore(Long chatId, int score) {
        QuizState currentState = userQuizState.get(chatId);
        if (currentState != null) {
            currentState.addScore(score);
            currentState.nextQuestion();
        }
    }

    public void endQuiz(Long chatId) {
        userQuizState.remove(chatId);
    }

    public static class QuizState {
        private int currentQuestionId = 1;
        private int score = 0;

        public int getCurrentQuestionId() { return currentQuestionId; }
        public int getScore() { return score; }

        public void nextQuestion() { this.currentQuestionId++; }
        public void addScore(int points) { this.score += points; }
    }
}
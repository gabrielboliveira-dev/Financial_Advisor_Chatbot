package br.com.financialchatbot.backend.infrastructure.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QuizStateCache {

    private final Map<Long, QuizState> userQuizState = new ConcurrentHashMap<>();

    public void startQuiz(Long chatId) {
        userQuizState.put(chatId, new QuizState(1, 0));
    }

    public Optional<QuizState> findByChatId(Long chatId) {
        return Optional.ofNullable(userQuizState.get(chatId));
    }

    public void updateUserScore(Long chatId, int score) {
        QuizState currentState = userQuizState.get(chatId);
        if (currentState != null) {
            currentState.setScore(currentState.getScore() + score);
        }
    }

    public void advanceQuestion(Long chatId) {
        QuizState currentState = userQuizState.get(chatId);
        if (currentState != null) {
            currentState.setCurrentQuestionId(currentState.getCurrentQuestionId() + 1);
        }
    }

    public void endQuiz(Long chatId) {
        userQuizState.remove(chatId);
    }

    public static class QuizState {
        private int currentQuestionId = 1;
        private int score = 0;

        public QuizState() {
        }

        public QuizState(int currentQuestionId, int score) {
            this.currentQuestionId = currentQuestionId;
            this.score = score;
        }

        public int getCurrentQuestionId() {
            return currentQuestionId;
        }

        public int getScore() {
            return score;
        }

        public void setCurrentQuestionId(int currentQuestionId) {
            this.currentQuestionId = currentQuestionId;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
package br.com.financialchatbot.backend.domain.entities;

import java.util.List;
import java.util.Map;

public record QuizQuestion(
        int id,
        String text,
        Map<String, Integer> options
) {
    public List<String> getOptionTexts() {
        return List.copyOf(options.keySet());
    }
}
package br.com.financialchatbot.backend.domain.services;

import br.com.financialchatbot.backend.domain.entities.QuizQuestion;
import br.com.financialchatbot.backend.domain.entities.RiskProfile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuizService {

    private final List<QuizQuestion> questions;

    public QuizService() {
        this.questions = List.of(
                new QuizQuestion(1,
                        "Por quanto tempo você pretende manter seus investimentos?",
                        Map.of("Menos de 1 ano", 1, "Entre 1 e 5 anos", 2, "Mais de 5 anos", 3)),
                new QuizQuestion(2,
                        "Qual sua familiaridade com produtos de investimento?",
                        Map.of("Nenhuma", 1, "Alguma, já investi um pouco", 2, "Muita, invisto regularmente", 3)),
                new QuizQuestion(3,
                        "Se seus investimentos caíssem 20% em um mês, o que você faria?",
                        Map.of("Venderia tudo", 1, "Manteria, mas não aportaria mais", 2, "Compraria mais, é uma oportunidade", 3))
        );
    }

    public Optional<QuizQuestion> getQuestionById(int id) {
        return questions.stream().filter(q -> q.id() == id).findFirst();
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public RiskProfile calculateProfile(int totalScore) {
        if (totalScore <= 4) {
            return RiskProfile.CONSERVADOR;
        } else if (totalScore <= 7) {
            return RiskProfile.MODERADO;
        } else {
            return RiskProfile.ARROJADO;
        }
    }
}
package br.com.financialchatbot.backend.domain.services;

import br.com.financialchatbot.backend.domain.entities.RiskProfile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AssetSuggestionService {

    private final Map<RiskProfile, List<String>> suggestionsByProfile;

    public AssetSuggestionService() {
        this.suggestionsByProfile = Map.of(
                RiskProfile.CONSERVADOR, List.of("ITUB4 (Itaú)", "BBAS3 (Banco do Brasil)", "TAEE11 (Taesa)"),
                RiskProfile.MODERADO, List.of("VALE3 (Vale)", "BBDC4 (Bradesco)", "WEGE3 (Weg)"),
                RiskProfile.ARROJADO, List.of("MGLU3 (Magazine Luiza)", "NUBR33 (Nubank)", "PETR4 (Petrobras)")
        );
    }

    public List<String> suggestFor(RiskProfile profile) {
        return suggestionsByProfile.getOrDefault(profile, List.of());
    }
}
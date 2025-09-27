package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.RiskProfile;
import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.UserGateway;
import br.com.financialchatbot.backend.domain.services.AssetSuggestionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class SuggestAssetUseCase {

    private final UserGateway userGateway;
    private final AssetSuggestionService suggestionService;

    public SuggestAssetUseCase(UserGateway userGateway, AssetSuggestionService suggestionService) {
        this.userGateway = userGateway;
        this.suggestionService = suggestionService;
    }

    public record Input(Long chatId) {}

    public record Output(List<String> suggestedAssets, boolean profileDefined) {}

    public Output execute(Input input) {
        User user = userGateway.findByChatId(input.chatId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + input.chatId()));

        if (user.riskProfile() == RiskProfile.NAO_DEFINIDO) {
            return new Output(List.of(), false);
        }

        List<String> suggestions = suggestionService.suggestFor(user.riskProfile());
        return new Output(suggestions, true);
    }
}
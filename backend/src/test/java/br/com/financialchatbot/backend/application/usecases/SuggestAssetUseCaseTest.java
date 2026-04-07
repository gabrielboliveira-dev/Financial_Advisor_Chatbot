package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.RiskProfile;
import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.UserGateway;
import br.com.financialchatbot.backend.domain.services.AssetSuggestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestAssetUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private AssetSuggestionService suggestionService;

    @InjectMocks
    private SuggestAssetUseCase useCase;

    private final Long chatId = 123456L;

    @Test
    void execute_UserNotFound_ThrowsException() {
        when(userGateway.findByChatId(chatId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> useCase.execute(new SuggestAssetUseCase.Input(chatId)));
    }

    @Test
    void execute_ProfileNotDefined_ReturnsEmptySuggestions() {
        User user = new User(1L, chatId, "User", RiskProfile.NAO_DEFINIDO);
        when(userGateway.findByChatId(chatId)).thenReturn(Optional.of(user));

        SuggestAssetUseCase.Output output = useCase.execute(new SuggestAssetUseCase.Input(chatId));

        assertFalse(output.profileDefined());
        assertTrue(output.suggestedAssets().isEmpty());
    }

    @Test
    void execute_ProfileDefined_ReturnsSuggestions() {
        User user = new User(1L, chatId, "User", RiskProfile.MODERADO);
        when(userGateway.findByChatId(chatId)).thenReturn(Optional.of(user));
        when(suggestionService.suggestFor(RiskProfile.MODERADO)).thenReturn(List.of("BTLG11", "IVVB11"));

        SuggestAssetUseCase.Output output = useCase.execute(new SuggestAssetUseCase.Input(chatId));

        assertTrue(output.profileDefined());
        assertEquals(2, output.suggestedAssets().size());
        assertTrue(output.suggestedAssets().contains("BTLG11"));
    }
}
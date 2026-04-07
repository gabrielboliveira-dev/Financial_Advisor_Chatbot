package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.RiskProfile;
import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrUpdateUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private CreateOrUpdateUserUseCase useCase;

    @Test
    void execute_UserExists_UpdatesUser() {
        Long chatId = 123L;
        String newName = "New Name";
        User existingUser = new User(1L, chatId, "Old Name", RiskProfile.MODERADO);
        User expectedUser = new User(1L, chatId, newName, RiskProfile.MODERADO);

        when(userGateway.findByChatId(chatId)).thenReturn(Optional.of(existingUser));
        when(userGateway.save(any(User.class))).thenReturn(expectedUser);

        User result = useCase.execute(new CreateOrUpdateUserUseCase.Input(chatId, newName));

        assertEquals(expectedUser, result);
        verify(userGateway).save(argThat(user -> user.firstName().equals(newName) && user.riskProfile() == RiskProfile.MODERADO));
    }

    @Test
    void execute_UserDoesNotExist_CreatesUser() {
        Long chatId = 123L;
        String name = "Name";
        User expectedUser = new User(1L, chatId, name, RiskProfile.NAO_DEFINIDO);

        when(userGateway.findByChatId(chatId)).thenReturn(Optional.empty());
        when(userGateway.save(any(User.class))).thenReturn(expectedUser);

        User result = useCase.execute(new CreateOrUpdateUserUseCase.Input(chatId, name));

        assertEquals(expectedUser, result);
        verify(userGateway).save(argThat(user -> user.id() == null && user.chatId().equals(chatId) && user.firstName().equals(name) && user.riskProfile() == RiskProfile.NAO_DEFINIDO));
    }
}
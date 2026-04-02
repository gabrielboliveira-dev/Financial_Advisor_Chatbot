package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import br.com.financialchatbot.backend.domain.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewPortfolioUseCaseTest {

    @Mock
    private PortfolioGateway portfolioGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private ViewPortfolioUseCase viewPortfolioUseCase;

    private final Long chatId = 123456L;
    private User mockUser;
    private Portfolio mockPortfolio;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, chatId, "John Doe", null);
        mockPortfolio = new Portfolio(1L, mockUser, Collections.emptyList());
    }

    @Test
    void execute_WhenPortfolioExists_ReturnsPortfolio() {
        // Arrange
        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.of(mockPortfolio));

        // Act
        Portfolio result = viewPortfolioUseCase.execute(new ViewPortfolioUseCase.Input(chatId));

        // Assert
        assertNotNull(result);
        assertEquals(mockPortfolio, result);
        verify(portfolioGateway, times(1)).findByUserChatId(chatId);
        verify(userGateway, never()).findByChatId(anyLong());
        verify(portfolioGateway, never()).save(any());
    }

    @Test
    void execute_WhenPortfolioDoesNotExistAndUserExists_CreatesAndReturnsNewPortfolio() {
        // Arrange
        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.empty());
        when(userGateway.findByChatId(chatId)).thenReturn(Optional.of(mockUser));
        when(portfolioGateway.save(any(Portfolio.class))).thenReturn(mockPortfolio);

        // Act
        Portfolio result = viewPortfolioUseCase.execute(new ViewPortfolioUseCase.Input(chatId));

        // Assert
        assertNotNull(result);
        assertEquals(mockPortfolio, result);
        verify(portfolioGateway, times(1)).findByUserChatId(chatId);
        verify(userGateway, times(1)).findByChatId(chatId);
        verify(portfolioGateway, times(1)).save(any(Portfolio.class));
    }

    @Test
    void execute_WhenPortfolioDoesNotExistAndUserDoesNotExist_ThrowsNoSuchElementException() {
        // Arrange
        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.empty());
        when(userGateway.findByChatId(chatId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> viewPortfolioUseCase.execute(new ViewPortfolioUseCase.Input(chatId))
        );

        assertEquals("User not found, cannot create portfolio.", exception.getMessage());
        verify(portfolioGateway, times(1)).findByUserChatId(chatId);
        verify(userGateway, times(1)).findByChatId(chatId);
        verify(portfolioGateway, never()).save(any(Portfolio.class));
    }
}

package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.AnalysisGateway;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyzePortfolioDiversificationUseCaseTest {

    @Mock
    private PortfolioGateway portfolioGateway;

    @Mock
    private AnalysisGateway analysisGateway;

    @InjectMocks
    private AnalyzePortfolioDiversificationUseCase useCase;

    @Test
    void execute_PortfolioExistsAndHasAssets_ReturnsImageBytes() {
        Long chatId = 123L;
        String ticker = "AAPL";
        PortfolioAsset asset = new PortfolioAsset(ticker, 10, new BigDecimal("100.00"));
        List<PortfolioAsset> assets = new ArrayList<>();
        assets.add(asset);
        Portfolio portfolio = new Portfolio(1L, new User(1L, chatId, "User", null), assets);

        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.of(portfolio));

        byte[] expectedBytes = new byte[]{1, 2, 3};
        when(analysisGateway.analyzeDiversification(assets)).thenReturn(expectedBytes);

        AnalyzePortfolioDiversificationUseCase.Output result = useCase.execute(new AnalyzePortfolioDiversificationUseCase.Input(chatId));

        assertArrayEquals(expectedBytes, result.imageBytes());
    }

    @Test
    void execute_PortfolioEmpty_ReturnsEmptyBytes() {
        Long chatId = 123L;
        Portfolio portfolio = new Portfolio(1L, new User(1L, chatId, "User", null), new ArrayList<>());

        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.of(portfolio));

        AnalyzePortfolioDiversificationUseCase.Output result = useCase.execute(new AnalyzePortfolioDiversificationUseCase.Input(chatId));

        assertArrayEquals(new byte[0], result.imageBytes());
    }

    @Test
    void execute_PortfolioNotFound_ThrowsException() {
        Long chatId = 123L;

        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> useCase.execute(new AnalyzePortfolioDiversificationUseCase.Input(chatId)));
    }
}
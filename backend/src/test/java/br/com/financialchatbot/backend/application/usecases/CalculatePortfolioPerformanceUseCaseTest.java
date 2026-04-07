package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.FinancialAssetGateway;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatePortfolioPerformanceUseCaseTest {

    @Mock
    private PortfolioGateway portfolioGateway;

    @Mock
    private FinancialAssetGateway financialAssetGateway;

    @InjectMocks
    private CalculatePortfolioPerformanceUseCase useCase;

    @Test
    void execute_PortfolioExists_CalculatesPerformance() {
        Long chatId = 123L;
        String ticker = "AAPL";
        PortfolioAsset asset = new PortfolioAsset(ticker, 10, new BigDecimal("100.00"));
        List<PortfolioAsset> assets = new ArrayList<>();
        assets.add(asset);
        Portfolio portfolio = new Portfolio(1L, new User(1L, chatId, "User", null), assets);

        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.of(portfolio));

        FinancialAsset currentAsset = new FinancialAsset(ticker, "Apple", "Market", new BigDecimal("150.00"));
        when(financialAssetGateway.findByTickers(List.of(ticker))).thenReturn(Map.of(ticker, currentAsset));

        CalculatePortfolioPerformanceUseCase.Performance result = useCase.execute(new CalculatePortfolioPerformanceUseCase.Input(chatId));

        assertEquals(new BigDecimal("1000.00"), result.totalInvested());
        assertEquals(new BigDecimal("1500.00"), result.currentValue());
        assertEquals(new BigDecimal("500.00"), result.profitOrLoss());
        assertEquals(new BigDecimal("50.0000"), result.returnPercentage());
    }

    @Test
    void execute_PortfolioEmpty_ReturnsZeroPerformance() {
        Long chatId = 123L;
        Portfolio portfolio = new Portfolio(1L, new User(1L, chatId, "User", null), new ArrayList<>());

        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.of(portfolio));

        CalculatePortfolioPerformanceUseCase.Performance result = useCase.execute(new CalculatePortfolioPerformanceUseCase.Input(chatId));

        assertEquals(BigDecimal.ZERO, result.totalInvested());
        assertEquals(BigDecimal.ZERO, result.currentValue());
        assertEquals(BigDecimal.ZERO, result.profitOrLoss());
        assertEquals(BigDecimal.ZERO, result.returnPercentage());
    }

    @Test
    void execute_PortfolioNotFound_ThrowsException() {
        Long chatId = 123L;

        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> useCase.execute(new CalculatePortfolioPerformanceUseCase.Input(chatId)));
    }
}
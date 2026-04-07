package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.entities.Portfolio;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddAssetToPortfolioUseCaseTest {

    @Mock
    private PortfolioGateway portfolioGateway;

    @Mock
    private ViewPortfolioUseCase viewPortfolioUseCase;

    @Mock
    private FinancialAssetGateway financialAssetGateway;

    @InjectMocks
    private AddAssetToPortfolioUseCase useCase;

    @Test
    void execute_WithProvidedPrice_AddsAsset() {
        Long chatId = 123L;
        String ticker = "PETR4";
        int quantity = 10;
        BigDecimal price = new BigDecimal("30.00");

        Portfolio portfolio = new Portfolio(1L, new User(1L, chatId, "User", null), new ArrayList<>());

        when(viewPortfolioUseCase.execute(any(ViewPortfolioUseCase.Input.class))).thenReturn(portfolio);
        when(portfolioGateway.save(any(Portfolio.class))).thenAnswer(i -> i.getArgument(0));

        Portfolio result = useCase.execute(new AddAssetToPortfolioUseCase.Input(chatId, ticker, quantity, price));

        assertEquals(1, result.getAssets().size());
        assertEquals("PETR4", result.getAssets().get(0).ticker());
        assertEquals(quantity, result.getAssets().get(0).quantity());
        assertEquals(price, result.getAssets().get(0).averagePrice());

        verify(financialAssetGateway, never()).findByTicker(anyString());
    }

    @Test
    void execute_WithoutPrice_FetchesPriceAndAddsAsset() {
        Long chatId = 123L;
        String ticker = "VALE3";
        int quantity = 5;

        Portfolio portfolio = new Portfolio(1L, new User(1L, chatId, "User", null), new ArrayList<>());
        FinancialAsset asset = new FinancialAsset("VALE3", "Vale S.A.", "B3", new BigDecimal("60.00"));

        when(viewPortfolioUseCase.execute(any(ViewPortfolioUseCase.Input.class))).thenReturn(portfolio);
        when(financialAssetGateway.findByTicker(ticker)).thenReturn(Optional.of(asset));
        when(portfolioGateway.save(any(Portfolio.class))).thenAnswer(i -> i.getArgument(0));

        Portfolio result = useCase.execute(new AddAssetToPortfolioUseCase.Input(chatId, ticker, quantity, BigDecimal.ZERO));

        assertEquals(1, result.getAssets().size());
        assertEquals("VALE3", result.getAssets().get(0).ticker());
        assertEquals(new BigDecimal("60.00"), result.getAssets().get(0).averagePrice());
    }

    @Test
    void execute_WithoutPriceAndAssetNotFound_ThrowsException() {
        Long chatId = 123L;
        String ticker = "UNKNOWN";

        Portfolio portfolio = new Portfolio(1L, new User(1L, chatId, "User", null), new ArrayList<>());

        when(viewPortfolioUseCase.execute(any(ViewPortfolioUseCase.Input.class))).thenReturn(portfolio);
        when(financialAssetGateway.findByTicker(ticker)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new AddAssetToPortfolioUseCase.Input(chatId, ticker, 10, BigDecimal.ZERO)));
    }
}
package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import br.com.financialchatbot.backend.domain.entities.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveAssetFromPortfolioUseCaseTest {

    @Mock
    private PortfolioGateway portfolioGateway;

    @InjectMocks
    private RemoveAssetFromPortfolioUseCase useCase;

    @Test
    void execute_PortfolioExists_RemovesAsset() {
        Long chatId = 123L;
        String ticker = "AAPL";
        PortfolioAsset asset = new PortfolioAsset(ticker, 10, new BigDecimal("150.00"));
        List<PortfolioAsset> assets = new ArrayList<>();
        assets.add(asset);
        Portfolio portfolio = new Portfolio(1L, new User(1L, chatId, "User", null), assets);

        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.of(portfolio));
        when(portfolioGateway.save(any(Portfolio.class))).thenAnswer(i -> i.getArgument(0));

        Portfolio result = useCase.execute(new RemoveAssetFromPortfolioUseCase.Input(chatId, ticker));

        assertEquals(0, result.getAssets().size());
    }

    @Test
    void execute_PortfolioNotFound_ThrowsException() {
        Long chatId = 123L;
        String ticker = "AAPL";

        when(portfolioGateway.findByUserChatId(chatId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> useCase.execute(new RemoveAssetFromPortfolioUseCase.Input(chatId, ticker)));
    }
}
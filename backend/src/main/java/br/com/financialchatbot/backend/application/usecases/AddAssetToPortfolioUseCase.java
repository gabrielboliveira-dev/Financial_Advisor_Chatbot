package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Component
public class AddAssetToPortfolioUseCase {

    private final PortfolioGateway portfolioGateway;
    private final ViewPortfolioUseCase viewPortfolioUseCase;

    public AddAssetToPortfolioUseCase(PortfolioGateway portfolioGateway, ViewPortfolioUseCase viewPortfolioUseCase) {
        this.portfolioGateway = portfolioGateway;
        this.viewPortfolioUseCase = viewPortfolioUseCase;
    }

    public record Input(Long chatId, String ticker, int quantity, BigDecimal price) {}

    @Transactional
    public Portfolio execute(Input input) {
        Portfolio portfolio = viewPortfolioUseCase.execute(new ViewPortfolioUseCase.Input(input.chatId()));
        PortfolioAsset assetToAdd = new PortfolioAsset(input.ticker().toUpperCase(), input.quantity(), input.price());
        portfolio.addAsset(assetToAdd);

        return portfolioGateway.save(portfolio);
    }
}
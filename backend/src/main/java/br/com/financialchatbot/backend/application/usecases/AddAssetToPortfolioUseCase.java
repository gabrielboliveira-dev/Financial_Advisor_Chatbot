package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import br.com.financialchatbot.backend.domain.gateways.FinancialAssetGateway;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Component
public class AddAssetToPortfolioUseCase {

    private final PortfolioGateway portfolioGateway;
    private final ViewPortfolioUseCase viewPortfolioUseCase;
    private final FinancialAssetGateway financialAssetGateway;

    public AddAssetToPortfolioUseCase(PortfolioGateway portfolioGateway, ViewPortfolioUseCase viewPortfolioUseCase, FinancialAssetGateway financialAssetGateway) {
        this.portfolioGateway = portfolioGateway;
        this.viewPortfolioUseCase = viewPortfolioUseCase;
        this.financialAssetGateway = financialAssetGateway;
    }

    public record Input(Long chatId, String ticker, int quantity, BigDecimal price) {}

    @Transactional
    public Portfolio execute(Input input) {
        Portfolio portfolio = viewPortfolioUseCase.execute(new ViewPortfolioUseCase.Input(input.chatId()));
        
        BigDecimal finalPrice = input.price();
        
        // Se o preço for 0 (opcional na regex), busca o preço atual na Brapi
        if (finalPrice == null || finalPrice.compareTo(BigDecimal.ZERO) == 0) {
            FinancialAsset asset = financialAssetGateway.findByTicker(input.ticker())
                    .orElseThrow(() -> new IllegalArgumentException("Ativo não encontrado ou indisponível. Não foi possível obter o preço atual."));
            finalPrice = asset.currentPrice();
        }

        PortfolioAsset assetToAdd = new PortfolioAsset(input.ticker().toUpperCase(), input.quantity(), finalPrice);
        portfolio.addAsset(assetToAdd);

        return portfolioGateway.save(portfolio);
    }
}

package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.gateways.FinancialAssetGateway;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Component
public class CalculatePortfolioPerformanceUseCase {

    private final PortfolioGateway portfolioGateway;
    private final FinancialAssetGateway financialAssetGateway;

    public CalculatePortfolioPerformanceUseCase(PortfolioGateway portfolioGateway, FinancialAssetGateway financialAssetGateway) {
        this.portfolioGateway = portfolioGateway;
        this.financialAssetGateway = financialAssetGateway;
    }

    public record Performance(
            BigDecimal totalInvested,
            BigDecimal currentValue,
            BigDecimal profitOrLoss,
            BigDecimal returnPercentage
    ) {}

    public record Input(Long chatId) {}

    public Performance execute(Input input) {
        Portfolio portfolio = portfolioGateway.findByUserChatId(input.chatId())
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if (portfolio.getAssets().isEmpty()) {
            return new Performance(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        List<String> tickers = portfolio.getAssets().stream().map(PortfolioAsset::ticker).toList();

        Map<String, FinancialAsset> currentData = financialAssetGateway.findByTickers(tickers);

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        for (PortfolioAsset asset : portfolio.getAssets()) {
            BigDecimal investedInAsset = asset.averagePrice().multiply(BigDecimal.valueOf(asset.quantity()));
            totalInvested = totalInvested.add(investedInAsset);

            FinancialAsset currentAssetData = currentData.get(asset.ticker());
            if (currentAssetData != null) {
                BigDecimal currentValueOfAsset = currentAssetData.currentPrice().multiply(BigDecimal.valueOf(asset.quantity()));
                currentValue = currentValue.add(currentValueOfAsset);
            } else {
                currentValue = currentValue.add(investedInAsset);
            }
        }

        BigDecimal profitOrLoss = currentValue.subtract(totalInvested);
        BigDecimal returnPercentage = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) != 0) {
            returnPercentage = profitOrLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        return new Performance(totalInvested, currentValue, profitOrLoss, returnPercentage);
    }
}
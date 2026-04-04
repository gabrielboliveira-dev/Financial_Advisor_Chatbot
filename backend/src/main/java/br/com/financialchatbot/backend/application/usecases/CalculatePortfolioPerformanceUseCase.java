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
import java.util.logging.Logger;

@Component
public class CalculatePortfolioPerformanceUseCase {

    private static final Logger LOGGER = Logger.getLogger(CalculatePortfolioPerformanceUseCase.class.getName());

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

        BigDecimal totalInvestedForCalculation = BigDecimal.ZERO;
        BigDecimal currentValueForCalculation = BigDecimal.ZERO;

        for (PortfolioAsset asset : portfolio.getAssets()) {
            FinancialAsset currentAssetData = currentData.get(asset.ticker());
            if (currentAssetData != null) {
                BigDecimal investedInAsset = asset.averagePrice().multiply(BigDecimal.valueOf(asset.quantity()));
                totalInvestedForCalculation = totalInvestedForCalculation.add(investedInAsset);

                BigDecimal currentValueOfAsset = currentAssetData.currentPrice().multiply(BigDecimal.valueOf(asset.quantity()));
                currentValueForCalculation = currentValueForCalculation.add(currentValueOfAsset);
            } else {
                LOGGER.warning("Could not fetch current price for ticker: " + asset.ticker() + ". This asset will be excluded from performance calculation.");
            }
        }

        BigDecimal profitOrLoss = currentValueForCalculation.subtract(totalInvestedForCalculation);
        BigDecimal returnPercentage = BigDecimal.ZERO;
        if (totalInvestedForCalculation.compareTo(BigDecimal.ZERO) != 0) {
            returnPercentage = profitOrLoss.divide(totalInvestedForCalculation, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        return new Performance(totalInvestedForCalculation, currentValueForCalculation, profitOrLoss, returnPercentage);
    }
}

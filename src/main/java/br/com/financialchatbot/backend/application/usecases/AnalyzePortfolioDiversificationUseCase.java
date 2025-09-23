package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.gateways.AnalysisGateway;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class AnalyzePortfolioDiversificationUseCase {

    private final PortfolioGateway portfolioGateway;
    private final AnalysisGateway analysisGateway;

    public AnalyzePortfolioDiversificationUseCase(PortfolioGateway portfolioGateway, AnalysisGateway analysisGateway) {
        this.portfolioGateway = portfolioGateway;
        this.analysisGateway = analysisGateway;
    }

    public record Input(Long chatId) {}

    public record Output(Map<String, Double> diversificationBySector) {}

    @Transactional(readOnly = true)
    public Output execute(Input input) {
        Portfolio portfolio = portfolioGateway.findByUserChatId(input.chatId())
                .orElseThrow(() -> new NoSuchElementException("Portfolio not found for user: " + input.chatId()));

        if (portfolio.getAssets().isEmpty()) {
            return new Output(Map.of());
        }

        AnalysisGateway.AnalysisResult result = analysisGateway.analyzeDiversification(portfolio.getAssets());

        return new Output(result.diversificationBySector());
    }
}
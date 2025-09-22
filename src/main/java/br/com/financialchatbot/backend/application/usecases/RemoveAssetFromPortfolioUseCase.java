package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Component
public class RemoveAssetFromPortfolioUseCase {

    private final PortfolioGateway portfolioGateway;

    public RemoveAssetFromPortfolioUseCase(PortfolioGateway portfolioGateway) {
        this.portfolioGateway = portfolioGateway;
    }

    public record Input(Long chatId, String ticker) {}

    @Transactional
    public Portfolio execute(Input input) {
        Portfolio portfolio = portfolioGateway.findByUserChatId(input.chatId())
                .orElseThrow(() -> new NoSuchElementException("Portfolio not found for user " + input.chatId()));
        portfolio.removeAsset(input.ticker());

        return portfolioGateway.save(portfolio);
    }
}
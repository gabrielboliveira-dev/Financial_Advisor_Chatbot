package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import br.com.financialchatbot.backend.domain.gateways.UserGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Importante!

import java.util.Collections;
import java.util.NoSuchElementException;

@Component
public class ViewPortfolioUseCase {

    private final PortfolioGateway portfolioGateway;
    private final UserGateway userGateway;

    public ViewPortfolioUseCase(PortfolioGateway portfolioGateway, UserGateway userGateway) {
        this.portfolioGateway = portfolioGateway;
        this.userGateway = userGateway;
    }

    public record Input(Long chatId) {}

    @Transactional
    public Portfolio execute(Input input) {
        return portfolioGateway.findByUserChatId(input.chatId())
                .orElseGet(() -> {
                    System.out.println("[USE CASE] Portfolio not found for user " + input.chatId() + ". Creating a new one.");

                    User user = userGateway.findByChatId(input.chatId())
                            .orElseThrow(() -> new NoSuchElementException("User not found, cannot create portfolio."));

                    Portfolio newPortfolio = new Portfolio(null, user, Collections.emptyList());

                    return portfolioGateway.save(newPortfolio);
                });
    }
}
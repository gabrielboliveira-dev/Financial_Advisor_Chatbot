package br.com.financialchatbot.backend.domain.gateways;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import java.util.Optional;

public interface PortfolioGateway {
    Portfolio save(Portfolio portfolio);
    Optional<Portfolio> findByUserChatId(Long chatId);
}
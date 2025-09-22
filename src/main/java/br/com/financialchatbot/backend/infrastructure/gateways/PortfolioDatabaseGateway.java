package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.gateways.PortfolioGateway;
import br.com.financialchatbot.backend.infrastructure.persistence.mappers.PortfolioMapper;
import br.com.financialchatbot.backend.infrastructure.persistence.repositories.PortfolioJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class PortfolioDatabaseGateway implements PortfolioGateway {

    private final PortfolioJpaRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;

    public PortfolioDatabaseGateway(PortfolioJpaRepository portfolioRepository, PortfolioMapper portfolioMapper) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioMapper = portfolioMapper;
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        var dataModel = portfolioMapper.toDataModel(portfolio);
        var savedDataModel = portfolioRepository.save(dataModel);
        return portfolioMapper.toDomainEntity(savedDataModel);
    }

    @Override
    public Optional<Portfolio> findByUserChatId(Long chatId) {
        return portfolioRepository.findByOwnerChatId(chatId)
                .map(portfolioMapper::toDomainEntity);
    }
}
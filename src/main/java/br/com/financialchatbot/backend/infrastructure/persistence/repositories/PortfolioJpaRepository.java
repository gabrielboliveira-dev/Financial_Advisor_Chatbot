package br.com.financialchatbot.backend.infrastructure.persistence.repositories;

import br.com.financialchatbot.backend.infrastructure.persistence.models.PortfolioDataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioJpaRepository extends JpaRepository<PortfolioDataModel, Long> {

    Optional<PortfolioDataModel> findByOwnerChatId(Long chatId);
}
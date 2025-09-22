package br.com.financialchatbot.backend.infrastructure.persistence.repositories;

import br.com.financialchatbot.backend.infrastructure.persistence.models.UserDataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserDataModel, Long> {
    Optional<UserDataModel> findByChatId(Long chatId);
}
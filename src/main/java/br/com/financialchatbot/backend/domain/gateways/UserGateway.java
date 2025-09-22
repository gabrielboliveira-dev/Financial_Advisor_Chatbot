package br.com.financialchatbot.backend.domain.gateways;

import br.com.financialchatbot.backend.domain.entities.User;
import java.util.Optional;

public interface UserGateway {
    User save(User user);
    Optional<User> findByChatId(Long chatId);
}
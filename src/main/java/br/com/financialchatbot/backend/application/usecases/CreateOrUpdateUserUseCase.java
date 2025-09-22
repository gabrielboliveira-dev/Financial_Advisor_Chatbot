package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.User;
import br.com.financialchatbot.backend.domain.gateways.UserGateway;
import org.springframework.stereotype.Component;

@Component
public class CreateOrUpdateUserUseCase {

    private final UserGateway userGateway;

    public CreateOrUpdateUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public record Input(Long chatId, String firstName) {}

    public User execute(Input input) {

        User user = userGateway.findByChatId(input.chatId())
                .map(existingUser -> {
                    System.out.println("[USE CASE] User found: " + existingUser.chatId());
                    return new User(existingUser.id(), existingUser.chatId(), input.firstName());
                })
                .orElseGet(() -> {
                    System.out.println("[USE CASE] User not found. Creating new user for chatId: " + input.chatId());
                    return new User(null, input.chatId(), input.firstName());
                });
        
        return userGateway.save(user);
    }
}
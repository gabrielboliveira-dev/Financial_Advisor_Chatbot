// Localização: infrastructure/telegram/FinancialAdvisorBot.java
package br.com.financialchatbot.backend.infrastructure.telegram;

import br.com.financialchatbot.backend.application.usecases.GetAssetInformationUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.NoSuchElementException;

@Component
public class FinancialAdvisorBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final GetAssetInformationUseCase getAssetInformationUseCase;

    public FinancialAdvisorBot(@Value("${telegram.bot.token}") String botToken,
                               @Value("${telegram.bot.username}") String botUsername,
                               GetAssetInformationUseCase getAssetInformationUseCase) {
        super(botToken);
        this.botUsername = botUsername;
        this.getAssetInformationUseCase = getAssetInformationUseCase;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().toUpperCase().trim();
            long chatId = update.getMessage().getChatId();

            try {
                var input = new GetAssetInformationUseCase.Input(messageText);

                var output = getAssetInformationUseCase.execute(input);

                String responseText = String.format(
                        "Ativo: %s (%s)\nMercado: %s\nPreço Atual: R$ %.2f",
                        output.tickerSymbol(),
                        output.companyName(),
                        output.market(),
                        output.currentPrice()
                );

                sendMessage(chatId, responseText);

            } catch (NoSuchElementException e) {
                sendMessage(chatId, "Desculpe, não encontrei o ativo: " + messageText);
            } catch (Exception e) {
                sendMessage(chatId, "Ocorreu um erro inesperado. Por favor, tente novamente.");
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }
}
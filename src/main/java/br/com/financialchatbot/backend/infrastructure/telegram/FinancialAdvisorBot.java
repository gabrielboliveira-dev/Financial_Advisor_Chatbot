package br.com.financialchatbot.backend.infrastructure.telegram;

import br.com.financialchatbot.backend.application.usecases.GetAssetInformationUseCase;
import br.com.financialchatbot.backend.domain.gateways.NluGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import br.com.financialchatbot.backend.application.usecases.CreateOrUpdateUserUseCase;


import java.util.NoSuchElementException;

@Component
public class FinancialAdvisorBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final GetAssetInformationUseCase getAssetInformationUseCase;
    private final NluGateway nluGateway;
    private final CreateOrUpdateUserUseCase createOrUpdateUserUseCase;

    public FinancialAdvisorBot(@Value("${telegram.bot.token}") String botToken,
                               @Value("${telegram.bot.username}") String botUsername,
                               GetAssetInformationUseCase getAssetInformationUseCase,
                               CreateOrUpdateUserUseCase createOrUpdateUserUseCase,
                               NluGateway nluGateway) {
        super(botToken);
        this.botUsername = botUsername;
        this.getAssetInformationUseCase = getAssetInformationUseCase;
        this.createOrUpdateUserUseCase = createOrUpdateUserUseCase;
        this.nluGateway = nluGateway;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            String messageText = message.getText().trim();
            long chatId = message.getChatId();
            String firstName = message.getFrom().getFirstName();

            var userInput = new CreateOrUpdateUserUseCase.Input(chatId, firstName);
            createOrUpdateUserUseCase.execute(userInput);

            nluGateway.interpret(messageText).ifPresentOrElse(intent -> {
                if ("get_asset_information".equals(intent.name())) {
                    String ticker = intent.entities().get("ticker");
                    if (ticker != null) {
                        executeGetAssetInfo(chatId, ticker);
                    } else {
                        sendMessage(chatId, "Entendi que você quer saber sobre um ativo, mas não identifiquei o código (ticker). Tente enviar algo como 'preço da PETR4'.");
                    }
                } else {
                    sendMessage(chatId, "Desculpe, ainda não sei como processar essa solicitação.");
                }
            }, () -> {
                sendMessage(chatId, "Desculpe, não entendi o que você quis dizer. Você pode perguntar sobre um ativo, por exemplo: 'qual a cotação da MGLU3?'");
            });
        }
    }

    private void executeGetAssetInfo(long chatId, String ticker) {
        try {
            var input = new GetAssetInformationUseCase.Input(ticker);
            var output = getAssetInformationUseCase.execute(input);
            String responseText = String.format(
                    "📈 **%s (%s)**\n\n" +
                    "🏢 **Mercado:** %s\n" +
                    "💰 **Preço Atual:** R$ %.2f",
                    output.tickerSymbol(),
                    output.companyName(),
                    output.market(),
                    output.currentPrice()
            );
            sendMessage(chatId, responseText);
        } catch (NoSuchElementException e) {
            sendMessage(chatId, "Desculpe, não encontrei informações para o ativo: " + ticker);
        } catch (Exception e) {
            sendMessage(chatId, "Ocorreu um erro inesperado ao buscar dados para " + ticker + ". Por favor, tente novamente mais tarde.");
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("Markdown");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Erro ao enviar mensagem para o chat ID " + chatId);
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }
}
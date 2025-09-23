package br.com.financialchatbot.backend.infrastructure.telegram;

import br.com.financialchatbot.backend.application.usecases.*;
import br.com.financialchatbot.backend.domain.entities.Portfolio;
import br.com.financialchatbot.backend.domain.gateways.NluGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class FinancialAdvisorBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final NluGateway nluGateway;
    private final CreateOrUpdateUserUseCase createOrUpdateUserUseCase;
    private final GetAssetInformationUseCase getAssetInformationUseCase;
    private final ViewPortfolioUseCase viewPortfolioUseCase;
    private final AddAssetToPortfolioUseCase addAssetToPortfolioUseCase;
    private final RemoveAssetFromPortfolioUseCase removeAssetFromPortfolioUseCase;
    private final CalculatePortfolioPerformanceUseCase calculatePortfolioPerformanceUseCase;

    public FinancialAdvisorBot(@Value("${telegram.bot.token}") String botToken,
                               @Value("${telegram.bot.username}") String botUsername,
                               NluGateway nluGateway,
                               CreateOrUpdateUserUseCase createOrUpdateUserUseCase,
                               GetAssetInformationUseCase getAssetInformationUseCase,
                               ViewPortfolioUseCase viewPortfolioUseCase,
                               AddAssetToPortfolioUseCase addAssetToPortfolioUseCase,
                               RemoveAssetFromPortfolioUseCase removeAssetFromPortfolioUseCase,
                               CalculatePortfolioPerformanceUseCase calculatePortfolioPerformanceUseCase) {
        super(botToken);
        this.botUsername = botUsername;
        this.nluGateway = nluGateway;
        this.createOrUpdateUserUseCase = createOrUpdateUserUseCase;
        this.getAssetInformationUseCase = getAssetInformationUseCase;
        this.viewPortfolioUseCase = viewPortfolioUseCase;
        this.addAssetToPortfolioUseCase = addAssetToPortfolioUseCase;
        this.removeAssetFromPortfolioUseCase = removeAssetFromPortfolioUseCase;
        this.calculatePortfolioPerformanceUseCase = calculatePortfolioPerformanceUseCase;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            String messageText = message.getText().trim();
            long chatId = message.getChatId();
            String firstName = message.getFrom().getFirstName();

            try {
                createOrUpdateUserUseCase.execute(new CreateOrUpdateUserUseCase.Input(chatId, firstName));
            } catch (Exception e) {
                handleGenericError(chatId, e);
                return;
            }

            nluGateway.interpret(messageText).ifPresentOrElse(intent -> {
                switch (intent.name()) {
                    case "get_asset_information" -> executeGetAssetInfo(chatId, intent.entities().get("ticker"));
                    case "view_portfolio" -> executeViewPortfolio(chatId);
                    case "add_asset" -> executeAddAsset(chatId, intent.entities());
                    case "remove_asset" -> executeRemoveAsset(chatId, intent.entities().get("ticker"));
                    case "calculate_performance" -> executeCalculatePerformance(chatId);
                    default -> sendMessage(chatId, "Desculpe, ainda não sei como processar essa solicitação.");
                }
            }, () -> sendMessage(chatId, "Desculpe, não entendi o que você quis dizer."));
        }
    }

    private void executeGetAssetInfo(long chatId, String ticker) {
        if (ticker == null) { sendMessage(chatId, "Não consegui identificar o código do ativo."); return; }
        try {
            var output = getAssetInformationUseCase.execute(new GetAssetInformationUseCase.Input(ticker));
            String responseText = String.format("📈 **%s (%s)**\n\n🏢 **Mercado:** %s\n💰 **Preço Atual:** R$ %.2f", output.tickerSymbol(), output.companyName(), output.market(), output.currentPrice());
            sendMessage(chatId, responseText);
        } catch (Exception e) { handleGenericError(chatId, e); }
    }

    private void executeViewPortfolio(long chatId) {
        try {
            Portfolio portfolio = viewPortfolioUseCase.execute(new ViewPortfolioUseCase.Input(chatId));
            StringBuilder response = new StringBuilder("💼 **Sua Carteira de Investimentos**\n\n");
            if (portfolio.getAssets().isEmpty()) {
                response.append("Você ainda não possui ativos na sua carteira.");
            } else {
                portfolio.getAssets().forEach(asset -> response.append(String.format("- **%s**: %d unidades a R$ %.2f (preço médio)\n", asset.ticker(), asset.quantity(), asset.averagePrice())));
            }
            sendMessage(chatId, response.toString());
        } catch (Exception e) { handleGenericError(chatId, e); }
    }

    private void executeAddAsset(long chatId, Map<String, String> entities) {
        try {
            String ticker = entities.get("ticker");
            int quantity = Integer.parseInt(entities.get("quantity"));
            BigDecimal price = new BigDecimal(entities.get("price"));
            addAssetToPortfolioUseCase.execute(new AddAssetToPortfolioUseCase.Input(chatId, ticker, quantity, price));
            sendMessage(chatId, String.format("✅ Ativo **%s** adicionado com sucesso à sua carteira!", ticker));
        } catch (Exception e) { handleGenericError(chatId, e); }
    }

    private void executeRemoveAsset(long chatId, String ticker) {
        if (ticker == null) { sendMessage(chatId, "Não consegui identificar o ativo a ser removido."); return; }
        try {
            removeAssetFromPortfolioUseCase.execute(new RemoveAssetFromPortfolioUseCase.Input(chatId, ticker));
            sendMessage(chatId, String.format("🗑️ Ativo **%s** removido com sucesso da sua carteira!", ticker));
        } catch (Exception e) { handleGenericError(chatId, e); }
    }

    private void handleGenericError(long chatId, Exception e) {
        if (e instanceof NoSuchElementException) {
            sendMessage(chatId, "Não encontrei as informações solicitadas.");
        } else {
            sendMessage(chatId, "Ocorreu um erro inesperado. Por favor, tente novamente.");
        }
        e.printStackTrace();
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("Markdown");
        try { execute(message); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    private void executeCalculatePerformance(long chatId) {
        try {
            var performance = calculatePortfolioPerformanceUseCase.execute(new CalculatePortfolioPerformanceUseCase.Input(chatId));

            String profitEmoji = performance.profitOrLoss().compareTo(BigDecimal.ZERO) >= 0 ? "🟢" : "🔴";
            String returnEmoji = performance.returnPercentage().compareTo(BigDecimal.ZERO) >= 0 ? "📈" : "📉";

            String responseText = String.format(
                    "📊 **Performance da Carteira**\n\n" +
                            "💰 **Valor Investido:** R$ %.2f\n" +
                            "🏦 **Valor Atual:** R$ %.2f\n" +
                            "%s **Lucro/Prejuízo:** R$ %.2f\n" +
                            "%s **Rentabilidade:** %.2f%%",
                    performance.totalInvested(),
                    performance.currentValue(),
                    profitEmoji, performance.profitOrLoss(),
                    returnEmoji, performance.returnPercentage()
            );
            sendMessage(chatId, responseText);
        } catch (Exception e) {
            handleGenericError(chatId, e);
        }
    }

    @Override
    public String getBotUsername() { return this.botUsername; }
}
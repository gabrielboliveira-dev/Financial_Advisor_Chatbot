package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.gateways.NluGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("fakeNluGateway")
public class FakeNluGateway implements NluGateway {

    private static final Pattern TICKER_PATTERN = Pattern.compile("([A-Z]{4}[0-9]{1,2})");
    private static final Pattern ADD_ASSET_PATTERN = Pattern.compile("(?:ADD|COMPRAR|ADICIONAR)\\s+(\\d+)\\s+(?:DE\\s+)?([A-Z]{4}[0-9]{1,2})\\s*(?:A|POR|@)?\\s*([\\d,.]+)");
    private static final Pattern REMOVE_ASSET_PATTERN = Pattern.compile("(?:REMOV|EXCLUIR)\\s+([A-Z]{4}[0-9]{1,2})");

    @Override
    public Optional<Intent> interpret(String text) {
        String upperCaseText = text.toUpperCase();

        Matcher addMatcher = ADD_ASSET_PATTERN.matcher(upperCaseText);
        if (addMatcher.find()) {
            Map<String, String> entities = new HashMap<>();
            entities.put("quantity", addMatcher.group(1));
            entities.put("ticker", addMatcher.group(2));
            entities.put("price", addMatcher.group(3).replace(",", "."));
            return Optional.of(new Intent("add_asset", entities));

        }if (upperCaseText.contains("PERFORMANCE") || upperCaseText.contains("DESEMPENHO") || upperCaseText.contains("LUCRO")) {
            return Optional.of(new Intent("calculate_performance", Map.of()));
        }


        Matcher removeMatcher = REMOVE_ASSET_PATTERN.matcher(upperCaseText);
        if (removeMatcher.find()) {
            return Optional.of(new Intent("remove_asset", Map.of("ticker", removeMatcher.group(1))));
        }

        if (upperCaseText.contains("PORTFOLIO") || upperCaseText.contains("CARTEIRA")) {
            return Optional.of(new Intent("view_portfolio", Map.of()));
        }

        Matcher tickerMatcher = TICKER_PATTERN.matcher(upperCaseText);
        if (tickerMatcher.find()) {
            return Optional.of(new Intent("get_asset_information", Map.of("ticker", tickerMatcher.group(1))));
        }

        return Optional.empty();
    }
}
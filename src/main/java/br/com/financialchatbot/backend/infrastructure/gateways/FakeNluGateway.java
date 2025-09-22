package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.gateways.NluGateway;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FakeNluGateway implements NluGateway {

    private static final Pattern TICKER_PATTERN = Pattern.compile("([A-Z]{4}[0-9]{1,2})");

    @Override
    public Optional<Intent> interpret(String text) {
        System.out.println("[NLU FAKE] Interpretando texto: " + text);
        String upperCaseText = text.toUpperCase();
        Matcher tickerMatcher = TICKER_PATTERN.matcher(upperCaseText);

        if (tickerMatcher.find()) {
            String ticker = tickerMatcher.group(1);
            Intent intent = new Intent("get_asset_information", Map.of("ticker", ticker));
            System.out.println("[NLU FAKE] Intenção encontrada: " + intent);
            return Optional.of(intent);
        }

        if (upperCaseText.contains("PORTFOLIO") || upperCaseText.contains("CARTEIRA")) {
            Intent intent = new Intent("view_portfolio", Collections.emptyMap());
            System.out.println("[NLU FAKE] Intenção encontrada: " + intent);
            return Optional.of(intent);
        }

        System.out.println("[NLU FAKE] Nenhuma intenção encontrada.");
        return Optional.empty();
    }
}
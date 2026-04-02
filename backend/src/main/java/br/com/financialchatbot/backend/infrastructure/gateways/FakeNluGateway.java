package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.gateways.NluGateway;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("fakeNluGateway")
public class FakeNluGateway implements NluGateway {

    private static final Pattern ADD_ASSET_PATTERN = Pattern.compile("(?:ADD|COMPRAR|ADICIONAR)\\s+(\\d+)\\s+(?:DE\\s+)?([A-Z]{4}[0-9]{1,2})(?:\\s*(?:A|POR|@)?\\s*([\\d,.]+))?");
    private static final Pattern REMOVE_ASSET_PATTERN = Pattern.compile("(?:REMOV|EXCLUIR)\\s+([A-Z]{4}[0-9]{1,2})");
    private static final Pattern TICKER_PATTERN = Pattern.compile("([A-Z]{4}[0-9]{1,2})");

    @Override
    public Optional<Intent> interpret(String text) {
        String upperCaseText = text.toUpperCase();
        System.out.println("[NLU FAKE] Interpretando texto: '" + upperCaseText + "'");

        Matcher addMatcher = ADD_ASSET_PATTERN.matcher(upperCaseText);
        if (addMatcher.find()) {
            Map<String, String> entities = new HashMap<>();
            entities.put("quantity", addMatcher.group(1));
            entities.put("ticker", addMatcher.group(2));
            
            // Opcional: Se o usuário não informar o preço, usamos "0" temporariamente. 
            // O backend ou use case idealmente lidaria com a busca do preço atual se fosse 0.
            // Para manter a compatibilidade com a regex sem quebrar a assinatura, fazemos o fallback.
            String priceGroup = addMatcher.group(3);
            if (priceGroup != null && !priceGroup.isBlank()) {
                entities.put("price", priceGroup.replace(",", "."));
            } else {
                 entities.put("price", "0"); // Precisa ser tratado no Use Case ou Controller. 
            }

            return Optional.of(new Intent("add_asset", entities));
        }

        Matcher removeMatcher = REMOVE_ASSET_PATTERN.matcher(upperCaseText);
        if (removeMatcher.find()) {
            return Optional.of(new Intent("remove_asset", Map.of("ticker", removeMatcher.group(1))));
        }

        if (upperCaseText.contains("QUIZ") || upperCaseText.contains("PERFIL")) {
            return Optional.of(new Intent("start_risk_profile_quiz", Map.of()));
        }

        if (upperCaseText.contains("SUGESTAO") || upperCaseText.contains("RECOMENDA") || upperCaseText.contains("SUGERIR")) {
            return Optional.of(new Intent("suggest_assets", Map.of()));
        }

        if (upperCaseText.contains("PORTFOLIO") || upperCaseText.contains("CARTEIRA")) {
            return Optional.of(new Intent("view_portfolio", Map.of()));
        }

        if (upperCaseText.contains("PERFORMANCE") || upperCaseText.contains("DESEMPENHO") || upperCaseText.contains("LUCRO")) {
            return Optional.of(new Intent("calculate_performance", Map.of()));
        }

        if (upperCaseText.contains("DIVERSIFICACAO") || upperCaseText.contains("ANALISE") || upperCaseText.contains("ANALISAR")) {
            return Optional.of(new Intent("analyze_diversification", Map.of()));
        }

        Matcher tickerMatcher = TICKER_PATTERN.matcher(upperCaseText);
        if (tickerMatcher.find()) {
            return Optional.of(new Intent("get_asset_information", Map.of("ticker", tickerMatcher.group(1))));
        }

        System.out.println("[NLU FAKE] Nenhuma intenção encontrada.");
        return Optional.empty();
    }
}

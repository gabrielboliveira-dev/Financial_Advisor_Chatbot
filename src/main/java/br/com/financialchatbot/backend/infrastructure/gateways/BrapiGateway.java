package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.gateways.FinancialAssetGateway;
import br.com.financialchatbot.backend.infrastructure.external.apis.dto.BrapiApiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BrapiGateway implements FinancialAssetGateway {

    private final WebClient webClient;

    public BrapiGateway(WebClient.Builder webClientBuilder, @Value("${brapi.api.url}") String apiUrl) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    @Override
    public Optional<FinancialAsset> findByTicker(String ticker) {
        try {
            BrapiApiResponseDto response = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/quote/{ticker}").build(ticker))
                    .retrieve()
                    .bodyToMono(BrapiApiResponseDto.class)
                    .block();

            if (response == null || response.results() == null || response.results().isEmpty()) {
                return Optional.empty();
            }

            var quote = response.results().get(0);

            FinancialAsset asset = new FinancialAsset(
                    quote.symbol(),
                    quote.longName(),
                    "B3",
                    quote.regularMarketPrice()
            );
            return Optional.of(asset);
        } catch (Exception e) {
            System.err.println("Erro ao buscar ativo na API Brapi: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Map<String, FinancialAsset> findByTickers(List<String> tickers) {
        if (tickers == null || tickers.isEmpty()) {
            return Collections.emptyMap();
        }

        String tickersAsString = String.join(",", tickers);

        try {
            BrapiApiResponseDto response = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/quote/{tickers}").build(tickersAsString))
                    .retrieve()
                    .bodyToMono(BrapiApiResponseDto.class)
                    .block();

            if (response == null || response.results() == null) {
                return Collections.emptyMap();
            }

            return response.results().stream()
                    .map(quote -> new FinancialAsset(
                            quote.symbol(),
                            quote.longName(),
                            "B3",
                            quote.regularMarketPrice()))
                    .collect(Collectors.toMap(FinancialAsset::tickerSymbol, asset -> asset));

        } catch (Exception e) {
            System.err.println("Erro ao buscar múltiplos ativos na API Brapi: " + e.getMessage());
            return Collections.emptyMap();
        }
    }
}
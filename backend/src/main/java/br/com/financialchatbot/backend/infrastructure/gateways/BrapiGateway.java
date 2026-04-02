package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.gateways.FinancialAssetGateway;
import br.com.financialchatbot.backend.infrastructure.external.apis.dto.BrapiApiResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    @CircuitBreaker(name = "brapi", fallbackMethod = "fallbackFindByTicker")
    public Optional<FinancialAsset> findByTicker(String ticker) {
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
    }

    @Override
    @CircuitBreaker(name = "brapi", fallbackMethod = "fallbackFindByTickers")
    public Map<String, FinancialAsset> findByTickers(List<String> tickers) {
        if (tickers == null || tickers.isEmpty()) {
            return Collections.emptyMap();
        }

        String tickersAsString = String.join(",", tickers);

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
    }

    // --- Fallback Methods ---
    
    public Optional<FinancialAsset> fallbackFindByTicker(String ticker, Throwable t) {
        System.err.println("Circuit Breaker aberto ou erro de rede ao buscar " + ticker + ": " + t.getMessage());
        return Optional.empty(); 
    }

    public Map<String, FinancialAsset> fallbackFindByTickers(List<String> tickers, Throwable t) {
        System.err.println("Circuit Breaker aberto ou erro de rede ao buscar lista de ativos: " + t.getMessage());
        return Collections.emptyMap(); 
    }
}

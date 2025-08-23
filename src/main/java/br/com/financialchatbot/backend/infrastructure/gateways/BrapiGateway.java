package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.gateways.FinancialAssetGateway;
import br.com.financialchatbot.backend.infrastructure.external.apis.dto.BrapiAssetResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class BrapiGateway implements FinancialAssetGateway {

    private final WebClient webClient;
    private final String apiUrl;

    public BrapiGateway(WebClient.Builder webClientBuilder, @Value("${brapi.api.url}") String apiUrl) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.apiUrl = apiUrl;
    }

    @Override
    public Optional<FinancialAsset> findByTicker(String ticker) {
        try {
            BrapiAssetResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/quote/{ticker}").build(ticker))
                    .retrieve()
                    .bodyToMono(BrapiAssetResponse.class)
                    .block();

            if (response == null) {
                return Optional.empty();
            }

            FinancialAsset asset = new FinancialAsset(
                    response.symbol(),
                    response.longName(),
                    response.market()
            );
            return Optional.of(asset);
        } catch (Exception e) {
            System.err.println("Erro ao buscar ativo na API Brapi: " + e.getMessage());
            return Optional.empty();
        }
    }
}
package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.gateways.FinancialAssetGateway;
import br.com.financialchatbot.backend.infrastructure.external.apis.dto.BrapiQuoteDto;
import br.com.financialchatbot.backend.infrastructure.external.apis.dto.BrapiApiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
                    "B3"
            );
            return Optional.of(asset);
        } catch (Exception e) {
            System.err.println("Erro ao buscar ativo na API Brapi: " + e.getMessage());
            return Optional.empty();
        }
    }
}
package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import br.com.financialchatbot.backend.domain.gateways.AnalysisGateway;
import br.com.financialchatbot.backend.infrastructure.gateways.dto.AnalysisRequestDto;
import br.com.financialchatbot.backend.infrastructure.gateways.dto.AnalysisResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

import java.util.List;

@Component
public class PythonAnalysisGateway implements AnalysisGateway {

    private final WebClient webClient;

    public PythonAnalysisGateway(WebClient.Builder webClientBuilder,
                                 @Value("${analysis.service.url}") String analysisServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(analysisServiceUrl).build();
    }

    @Override
    public AnalysisResult analyzeDiversification(List<PortfolioAsset> assets) {
        System.out.println("[GATEWAY] Enviando " + assets.size() + " ativos para análise de diversificação...");

        var requestAssets = assets.stream()
                .map(asset -> new AnalysisRequestDto.AssetPayload(
                        asset.ticker(),
                        asset.quantity(),
                        asset.averagePrice()))
                .toList();
        var requestDto = new AnalysisRequestDto(requestAssets);

        try {
            AnalysisResponseDto response = webClient.post()
                    .uri("/analyze/diversification")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(AnalysisResponseDto.class)
                    .block();

            if (response != null) {
                return new AnalysisResult(response.diversificationBySector());
            }

        } catch (Exception e) {
            System.err.println("Erro ao se comunicar com o serviço de análise: " + e.getMessage());
        }

        return new AnalysisResult(Map.of());
    }
}
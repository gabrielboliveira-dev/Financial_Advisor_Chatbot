package br.com.financialchatbot.backend.infrastructure.gateways;

import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import br.com.financialchatbot.backend.domain.gateways.AnalysisGateway;
import br.com.financialchatbot.backend.infrastructure.gateways.dto.AnalysisRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class PythonAnalysisGateway implements AnalysisGateway {

    private final WebClient webClient;

    public PythonAnalysisGateway(WebClient.Builder webClientBuilder,
                                 @Value("${analysis.service.url}") String analysisServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(analysisServiceUrl).build();
    }

    @Override
    public byte[] analyzeDiversification(List<PortfolioAsset> assets) {
        var requestAssets = assets.stream()
                .map(asset -> new AnalysisRequestDto.AssetPayload(
                        asset.ticker(),
                        asset.quantity(),
                        asset.averagePrice()))
                .toList();
        var requestDto = new AnalysisRequestDto(requestAssets);

        try {
            return webClient.post()
                    .uri("/analyze/diversification")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

        } catch (Exception e) {
            System.err.println("Erro ao se comunicar com o serviço de análise: " + e.getMessage());
        }

        return new byte[0];
    }
}
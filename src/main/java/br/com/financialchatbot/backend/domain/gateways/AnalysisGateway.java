package br.com.financialchatbot.backend.domain.gateways;

import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AnalysisGateway {
    AnalysisResult analyzeDiversification(List<PortfolioAsset> assets);

    record AnalysisResult(Map<String, Double> diversificationBySector) {}
}
package br.com.financialchatbot.backend.domain.gateways;

import br.com.financialchatbot.backend.domain.entities.PortfolioAsset;
import java.util.List;

public interface AnalysisGateway {

    byte[] analyzeDiversification(List<PortfolioAsset> assets);

}
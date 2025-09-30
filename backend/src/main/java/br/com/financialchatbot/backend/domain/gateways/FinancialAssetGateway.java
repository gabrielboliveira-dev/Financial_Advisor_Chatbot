package br.com.financialchatbot.backend.domain.gateways;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FinancialAssetGateway {

    Optional<FinancialAsset> findByTicker(String ticker);

    Map<String, FinancialAsset> findByTickers(List<String> tickers);
}
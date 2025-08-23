package br.com.financialchatbot.backend.domain.gateways;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;

import java.util.Optional;

public interface FinancialAssetGateway {

    Optional<FinancialAsset> findByTicker(String ticker);
}
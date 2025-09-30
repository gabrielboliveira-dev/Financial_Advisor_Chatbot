package br.com.financialchatbot.backend.infrastructure.gateways.dto;

import java.math.BigDecimal;
import java.util.List;

public record AnalysisRequestDto(List<AssetPayload> assets) {
    public record AssetPayload(String ticker, int quantity, BigDecimal averagePrice) {}
}
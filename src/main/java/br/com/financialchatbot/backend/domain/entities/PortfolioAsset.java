package br.com.financialchatbot.backend.domain.entities;

import java.math.BigDecimal;

public record PortfolioAsset(
        String ticker,
        int quantity,
        BigDecimal averagePrice
) {

    public PortfolioAsset {
        if (ticker == null || ticker.isBlank()) {
            throw new IllegalArgumentException("Ticker cannot be null or blank.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        if (averagePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Average price cannot be negative.");
        }
    }
}
package br.com.financialchatbot.backend.domain.entities;

import java.math.BigDecimal;

public record FinancialAsset(
        String tickerSymbol,
        String companyName,
        String market,
        BigDecimal currentPrice
    ) {

    public FinancialAsset(String tickerSymbol, String companyName, String market, BigDecimal currentPrice) {

        this.tickerSymbol = tickerSymbol.trim().toUpperCase();
        this.companyName = companyName;
        this.market = market;
        this.currentPrice = (currentPrice != null) ? currentPrice : BigDecimal.ZERO;
    }
}

package br.com.financialchatbot.backend.domain.entities;

public class FinancialAsset {

    private final String tickerSymbol;
    private final String companyName;
    private final String market;

    public FinancialAsset(String tickerSymbol, String companyName, String market) {
        if (tickerSymbol == null || tickerSymbol.trim().isEmpty()) {
            throw new IllegalArgumentException("O símbolo do ativo (ticker) não pode ser nulo ou vazio.");
        }

        this.tickerSymbol = tickerSymbol.trim().toUpperCase();
        this.companyName = companyName;
        this.market = market;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getMarket() {
        return market;
    }
}

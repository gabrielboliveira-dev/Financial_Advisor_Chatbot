package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import org.springframework.stereotype.Component;

@Component
public class GetAssetInformationUseCase {

    public Output execute(Input input) {
        System.out.println("Buscando informações para o ticker: " + input.tickerSymbol());

        var asset = new FinancialAsset(input.tickerSymbol(), "Empresa Fictícia S.A.", "B3");

        return new Output(
                asset.getTickerSymbol(),
                asset.getCompanyName(),
                asset.getMarket(),
                100.50,
                -0.05
        );
    }
    public record Input(String tickerSymbol) {}

    public record Output(String tickerSymbol, String companyName, String market, double currentPrice, double dailyChange) {}
}
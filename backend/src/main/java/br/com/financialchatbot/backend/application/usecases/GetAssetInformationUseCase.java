package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.gateways.FinancialAssetGateway;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class GetAssetInformationUseCase {

    private final FinancialAssetGateway assetGateway;

    public GetAssetInformationUseCase(FinancialAssetGateway assetGateway) {
        this.assetGateway = assetGateway;
    }

    public record Input(String tickerSymbol) {}

    public record Output(String tickerSymbol, String companyName, String market, double currentPrice, double dailyChange) {}

    public Output execute(Input input) {
        FinancialAsset asset = assetGateway.findByTicker(input.tickerSymbol())
                .orElseThrow(() -> new NoSuchElementException("Ativo não encontrado: " + input.tickerSymbol()));

        return new Output(
                asset.tickerSymbol(),
                asset.companyName(),
                asset.market(),
                asset.currentPrice().doubleValue(),
                -0.05
        );
    }
}
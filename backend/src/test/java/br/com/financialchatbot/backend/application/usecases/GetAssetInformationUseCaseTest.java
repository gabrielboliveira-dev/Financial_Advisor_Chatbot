package br.com.financialchatbot.backend.application.usecases;

import br.com.financialchatbot.backend.domain.entities.FinancialAsset;
import br.com.financialchatbot.backend.domain.gateways.FinancialAssetGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssetInformationUseCaseTest {

    @Mock
    private FinancialAssetGateway assetGateway;

    @InjectMocks
    private GetAssetInformationUseCase useCase;

    @Test
    void execute_AssetExists_ReturnsInformation() {
        String ticker = "AAPL";
        FinancialAsset asset = new FinancialAsset(ticker, "Apple Inc.", "NASDAQ", new BigDecimal("150.00"));
        when(assetGateway.findByTicker(ticker)).thenReturn(Optional.of(asset));

        GetAssetInformationUseCase.Output output = useCase.execute(new GetAssetInformationUseCase.Input(ticker));

        assertEquals(ticker, output.tickerSymbol());
        assertEquals("Apple Inc.", output.companyName());
        assertEquals("NASDAQ", output.market());
        assertEquals(150.00, output.currentPrice());
        assertEquals(-0.05, output.dailyChange());
    }

    @Test
    void execute_AssetNotFound_ThrowsException() {
        String ticker = "UNKNOWN";
        when(assetGateway.findByTicker(ticker)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> useCase.execute(new GetAssetInformationUseCase.Input(ticker)));
    }
}
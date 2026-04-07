package br.com.financialchatbot.backend.infrastructure.api.controllers;

import br.com.financialchatbot.backend.application.usecases.GetAssetInformationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AssetController.class)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetAssetInformationUseCase getAssetInformationUseCase;

    @Test
    void getAssetByTicker_Success_ReturnsOkAndAsset() throws Exception {
        String ticker = "AAPL";
        GetAssetInformationUseCase.Output output = new GetAssetInformationUseCase.Output(
                "AAPL", "Apple Inc.", "NASDAQ", 150.00, -0.05
        );

        when(getAssetInformationUseCase.execute(any(GetAssetInformationUseCase.Input.class))).thenReturn(output);

        mockMvc.perform(get("/api/v1/assets/{ticker}", ticker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tickerSymbol").value("AAPL"))
                .andExpect(jsonPath("$.companyName").value("Apple Inc."))
                .andExpect(jsonPath("$.market").value("NASDAQ"))
                .andExpect(jsonPath("$.currentPrice").value(150.00))
                .andExpect(jsonPath("$.dailyChange").value(-0.05));
    }

    @Test
    void getAssetByTicker_NotFound_ReturnsNotFound() throws Exception {
        String ticker = "UNKNOWN";

        when(getAssetInformationUseCase.execute(any(GetAssetInformationUseCase.Input.class)))
                .thenThrow(new NoSuchElementException("Ativo não encontrado: " + ticker));

        mockMvc.perform(get("/api/v1/assets/{ticker}", ticker))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAssetByTicker_Exception_ReturnsInternalServerError() throws Exception {
        String ticker = "ERROR";

        when(getAssetInformationUseCase.execute(any(GetAssetInformationUseCase.Input.class)))
                .thenThrow(new RuntimeException("Algum erro interno"));

        mockMvc.perform(get("/api/v1/assets/{ticker}", ticker))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Algum erro interno"));
    }
}
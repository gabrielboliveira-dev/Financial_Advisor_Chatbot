package br.com.financialchatbot.backend.infrastructure.api.controllers;

import br.com.financialchatbot.backend.application.usecases.GetAssetInformationUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final GetAssetInformationUseCase getAssetInformationUseCase;

    public AssetController(GetAssetInformationUseCase getAssetInformationUseCase) {
        this.getAssetInformationUseCase = getAssetInformationUseCase;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<Object> getAssetByTicker(@PathVariable String ticker) {
        try {
            var input = new GetAssetInformationUseCase.Input(ticker.toUpperCase());

            var output = getAssetInformationUseCase.execute(input);

            return ResponseEntity.ok(output);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
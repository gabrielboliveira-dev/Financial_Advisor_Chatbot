package br.com.financialchatbot.backend.infrastructure.external.apis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrapiAssetResponse(
        String symbol,
        String longName,
        String market
) {}
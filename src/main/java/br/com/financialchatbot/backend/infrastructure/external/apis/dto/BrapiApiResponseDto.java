package br.com.financialchatbot.backend.infrastructure.external.apis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrapiApiResponseDto(
        List<BrapiQuoteDto> results
) {}
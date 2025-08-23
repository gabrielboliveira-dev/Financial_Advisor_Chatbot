package br.com.financialchatbot.backend.infrastructure.external.apis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrapiQuoteDto(
                             String symbol,
                             String longName

) {}
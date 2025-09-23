package br.com.financialchatbot.backend.infrastructure.gateways.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalysisResponseDto(Map<String, Double> diversificationBySector) {}
package br.com.financialchatbot.backend.domain.entities;

public record User(
        Long id,
        Long chatId,
        String firstName
) {}
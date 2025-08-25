package br.com.financialchatbot.backend.domain.gateways;

import java.util.Map;
import java.util.Optional;

public interface NluGateway {
    Optional<Intent> interpret(String text);

    record Intent(String name, Map<String, String> entities) {}
}

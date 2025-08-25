package br.com.financialchatbot.backend;

import br.com.financialchatbot.backend.infrastructure.telegram.FinancialAdvisorBot;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	private final FinancialAdvisorBot financialAdvisorBot;

	public BackendApplication(FinancialAdvisorBot financialAdvisorBot) {
		this.financialAdvisorBot = financialAdvisorBot;
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("=================================================");
		System.out.println(">>> REGISTRANDO O BOT MANUALMENTE...");
		System.out.println("=================================================");
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(this.financialAdvisorBot);
			System.out.println(">>> BOT REGISTRADO COM SUCESSO! Ouvindo por mensagens...");
		} catch (TelegramApiException e) {
			System.err.println("!!! ERRO AO REGISTRAR O BOT !!!");
			e.printStackTrace();
		}
	}
}
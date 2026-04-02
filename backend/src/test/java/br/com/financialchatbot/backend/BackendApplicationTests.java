package br.com.financialchatbot.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "telegram.bot.token=dummy_token",
        "telegram.bot.username=dummy_username",
        "analysis.service.url=http://dummy-url:5001",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
